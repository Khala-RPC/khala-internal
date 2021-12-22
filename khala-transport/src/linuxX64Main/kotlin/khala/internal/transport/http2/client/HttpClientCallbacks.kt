package khala.internal.transport.http2.client

import khala.internal.transport.cinterop.czmq.setsockopt
import khala.internal.transport.cinterop.libevent.*
import khala.internal.transport.cinterop.libevent.ssize_t
import khala.internal.transport.cinterop.nghttp2.*
import kotlinx.cinterop.*
import mu.KotlinLogging
import platform.posix.IPPROTO_TCP
import platform.posix.TCP_NODELAY
import platform.posix.int32_t
import platform.posix.size_t
import platform.posix.uint8_t
import kotlin.experimental.and

private val logger = KotlinLogging.logger {}

/**
 * The eventcb() is invoked by the libevent event loop when an event
 * (e.g. connection has been established, timeout, etc.)
 * occurs on the underlying network socket.
 */
// eventcb
internal fun clientEventCallback(
    bufferEvent: CPointer<bufferevent>?,
    events: Short,
    ptr: COpaquePointer?
) = memScoped {
    val sessionData = ptr?.asStableRef<HttpClientSessionData>()?.get()
    when {
        (events.toUInt() and BEV_EVENT_CONNECTED.toUInt()) != 0u -> {
            val fd = bufferevent_getfd(bufferEvent)
            val arr = allocArray<ByteVar>(1)
            arr[0] = 1
            //TODO SSL Stuff (see https://nghttp2.org/documentation/tutorial-client.html)
            logger.info { "Connected" }
            setsockopt(fd, IPPROTO_TCP, TCP_NODELAY, arr, 1)
            initializeClientHttpSession(sessionData)
            sendClientConnectionHeader(sessionData)
            clientSubmitRequest(sessionData)
            if (clientSessionSend(sessionData) != 0) {
                deleteClientHttpSessionData(sessionData)
            }
            return@memScoped
        }
        (events.toUInt() and BEV_EVENT_EOF.toUInt()) != 0u -> {
            //TODO
            logger.warn { "Disconnected from the remote host" }
        }
        (events.toUInt() and BEV_EVENT_ERROR.toUInt()) != 0u -> {
            //TODO
            logger.warn { "Network error" }
        }
        (events.toUInt() and BEV_EVENT_TIMEOUT.toUInt()) != 0u -> {
            //TODO
            logger.warn { "Timeout" }
        }
    }
    deleteClientHttpSessionData(sessionData)
}

/**
 * Called when data is available to read from the bufferevent input buffer.
 * In this function we feed all unprocessed,
 * received data to the nghttp2 session object using the nghttp2_session_mem_recv() function.
 * nghttp2_session_mem_recv() processes the received data and may invoke nghttp2 callbacks and queue frames for transmission.
 * Since there may be pending frames for transmission, we call immediately session_send() to send them.
 */
// readcb
internal fun clientReadCallback(
    bufferEvent: CPointer<bufferevent>?,
    ptr: COpaquePointer?
) {
    val sessionData = ptr?.asStableRef<HttpClientSessionData>()?.get()
    val input = bufferevent_get_input(bufferEvent)
    val dataLength = evbuffer_get_length(input)
    val data = evbuffer_pullup(input, -1)
    val readLength = nghttp2_session_mem_recv(sessionData?.session, data, dataLength)
    if (readLength < 0) {
        logger.warn { "Fatal error: ${nghttp2_strerror(readLength.convert())}" }
        deleteClientHttpSessionData(sessionData)
        return
    }
    if (evbuffer_drain(input, readLength.convert()) != 0) {
        logger.warn { "Fatal error: evbuffer_drain failed" }
        deleteClientHttpSessionData(sessionData)
        return
    }
    if (clientSessionSend(sessionData) != 0) {
        deleteClientHttpSessionData(sessionData)
        return
    }
}

// send_callback
//TODO Limit the amount of buffered data (see https://nghttp2.org/documentation/tutorial-client.html)
internal fun clientSendCallback(
    session: CPointer<nghttp2_session>?,
    data: CPointer<UByteVar>?,
    length: size_t,
    flags: Int,
    userData: COpaquePointer?
): ssize_t {
    val sessionData = userData?.asStableRef<HttpClientSessionData>()?.get()
    val bufferEvent = sessionData?.bufferEvent
    bufferevent_write(bufferEvent, data, length)
    return length.convert()
}

// writecb
internal fun clientWriteCallback(
    bufferEvent: CPointer<bufferevent>?,
    ptr: COpaquePointer?
) {
    val sessionData = ptr?.asStableRef<HttpClientSessionData>()?.get()
    if (nghttp2_session_want_read(sessionData?.session) == 0 &&
        nghttp2_session_want_write(sessionData?.session) == 0 &&
        evbuffer_get_length(bufferevent_get_output(sessionData?.bufferEvent)) == 0uL) {
        deleteClientHttpSessionData(sessionData)
    }
}

/**
 * Called when nghttp2 library gets started to receive header block.
 */
// on_begin_headers_callback
internal fun clientOnBeginHeadersCallback(
    session: CPointer<nghttp2_session>?,
    frame: CPointer<nghttp2_frame>?,
    userData: COpaquePointer?
): Int {
    val sessionData = userData?.asStableRef<HttpClientSessionData>()?.get()
    when (frame?.pointed?.hd?.type?.toUInt()) {
        NGHTTP2_HEADERS -> {
            if (frame.pointed.headers.cat == NGHTTP2_HCAT_RESPONSE &&
                sessionData?.streamData?.streamId == frame.pointed.hd.stream_id) {
                logger.info { "Response headers for stream ${frame.pointed.hd.stream_id}" }
            }
        }
    }
    return 0
}

/**
 * Called on each received header.
 */
// on_header_callback
internal fun clientOnHeaderCallback(
    session: CPointer<nghttp2_session>?,
    frame: CPointer<nghttp2_frame>?,
    name: CPointer<UByteVar>?,
    nameLength: size_t,
    value: CPointer<UByteVar>?,
    valueLength: size_t,
    flags: uint8_t,
    userData: COpaquePointer?
): Int {
    val sessionData = userData?.asStableRef<HttpClientSessionData>()?.get()
    when (frame?.pointed?.hd?.type?.toUInt()) {
        NGHTTP2_HEADERS -> {
            if (frame.pointed.headers.cat == NGHTTP2_HCAT_RESPONSE &&
                sessionData?.streamData?.streamId == frame.pointed.hd.stream_id) {
                logger.info { "Received response headers: $name $nameLength $value $valueLength" }
            }
        }
    }
    return 0
}

/**
 * Called when a frame has been fully received.
 */
// on_frame_recv_callback
internal fun clientOnFrameRecvCallback(
    session: CPointer<nghttp2_session>?,
    frame: CPointer<nghttp2_frame>?,
    userData: COpaquePointer?
): Int {
    val sessionData = userData?.asStableRef<HttpClientSessionData>()?.get()
    when (frame?.pointed?.hd?.type?.toUInt()) {
        NGHTTP2_HEADERS -> {
            if (frame.pointed.headers.cat == NGHTTP2_HCAT_RESPONSE &&
                sessionData?.streamData?.streamId == frame.pointed.hd.stream_id) {
                logger.info { "All headers received" }
            }
        }
    }
    return 0
}

/**
 * Called when a chunk of data is received from the remote peer.
 */
// on_data_chunk_recv_callback
//TODO Data frame processing
internal fun clientOnDataChunkRecvCallback(
    session: CPointer<nghttp2_session>?,
    flags: uint8_t,
    streamId: int32_t,
    data: CPointer<UByteVar>?,
    length: size_t,
    userData: COpaquePointer?
): Int {
    val sessionData = userData?.asStableRef<HttpClientSessionData>()?.get()
    if (sessionData?.streamData?.streamId == streamId) {
        fwrite(data, length, 1, platform.posix.stdout?.reinterpret())
    }
    return 0
}

/**
 * Called when the stream is about to close.
 * If the stream ID matches the one we initiated, it means that its stream is going to be closed.
 * Since we have finished receiving resource we wanted (or the stream was reset by RST_STREAM from the remote peer),
 * we call nghttp2_session_terminate_session() to commence closure of the HTTP/2 session gracefully.
 * If you have some data associated for the stream to be closed, you may delete it here.
 */
// on_stream_close_callback
internal fun clientOnStreamCloseCallback(
    session: CPointer<nghttp2_session>?,
    streamId: int32_t,
    errorCode: nghttp2_error_code,
    userData: COpaquePointer?
): Int {
    val sessionData = userData?.asStableRef<HttpClientSessionData>()?.get()
    if (sessionData?.streamData?.streamId == streamId) {
        logger.warn { "Stream $streamId closed with error code $errorCode" }
        val rv = nghttp2_session_terminate_session(session, NGHTTP2_NO_ERROR)
        if (rv != 0) {
            return NGHTTP2_ERR_CALLBACK_FAILURE
        }
    }
    return 0
}