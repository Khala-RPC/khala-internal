package khala.internal.http2.server

import cnames.structs.evconnlistener
import khala.internal.cinterop.libevent.*
import khala.internal.cinterop.nghttp2.*
import khala.internal.cinterop.nghttp2.uint8_t
import kotlinx.cinterop.*
import mu.KotlinLogging
import platform.posix.int32_t
import platform.posix.memcmp
import platform.posix.size_t
import platform.posix.uint32_t

private val logger = KotlinLogging.logger {}

/**
 * Called when a new connection is accepted.
 * Initializes HttpSessionData and bufferevent, registers callbacks for the created connection.
 */
internal fun newConnectionAcceptCallback(
    eventConnectionListener: CPointer<evconnlistener>?,
    fd: Int,
    socketAddress: CPointer<sockaddr>?,
    addressLength: Int,
    arg: COpaquePointer
) {
    val httpContext: StableRef<HttpContext> = arg.asStableRef()
    val sessionData: HttpSessionData = createHttpSessionData()
    bufferevent_setcb(
        sessionData.bufferEvent,
        readCallback,
        writeCallback,
        eventCallback,
        sessionData.stableRef.asCPointer()
    )
}

/**
 * Called when an event (e.g. connection has been established, timeout, etc.) occurs on the underlying network socket.
 */
internal fun serverEventCallback(
    bufferEvent: CPointer<bufferevent>?,
    events: Short,
    ptr: COpaquePointer
) {
    val sessionData = ptr.asStableRef<HttpSessionData>().get()
    if ((events.toUInt() and BEV_EVENT_CONNECTED.toUInt()) != 0u) {
        logger.debug { "Client ${sessionData.clientAddress} connected" }
        //TODO ALPN & SSL stuff. See https://nghttp2.org/documentation/tutorial-server.html
        initializeHttpSession(sessionData)
        if (sendServerConnectionHeader(sessionData) != 0 ||
                sessionSend(sessionData) != 0) {
            deleteHttpSessionData(sessionData)
        }
        return
    }
    //TODO Improved error handling? Right now we just drop the connection
    when {
        (events.toUInt() and BEV_EVENT_EOF.toUInt()) != 0u -> {
            logger.error { "EOF ${sessionData.clientAddress}" }
        }
        (events.toUInt() and BEV_EVENT_ERROR.toUInt()) != 0u -> {
            logger.error { "Network error ${sessionData.clientAddress}" }
        }
        (events.toUInt() and BEV_EVENT_TIMEOUT.toUInt()) != 0u -> {
            logger.error { "Timeout ${sessionData.clientAddress}" }
        }
    }
    deleteHttpSessionData(sessionData)
}

/**
 * Called when data is available to read in the bufferevent input buffer.
 * We just call [sessionRecv] to process incoming data.
 */
internal fun serverReadCallback(
    bufferEvent: CPointer<bufferevent>?,
    ptr: COpaquePointer
) {
    val sessionData = ptr.asStableRef<HttpSessionData>().get()
    if (sessionRecv(sessionData) != 0) {
        deleteHttpSessionData(sessionData)
    }
}

/**
 * Called when all data in the bufferevent output buffer has been sent.
 * First we check whether we should drop the connection or not.
 * The nghttp2 session object keeps track of reception and transmission of GOAWAY frames
 * and other error conditions as well.
 * Using this information, the nghttp2 session object can state whether the connection should be dropped or not.
 * More specifically, if both nghttp2_session_want_read() and nghttp2_session_want_write() return 0,
 * the connection is no-longer required and can be closed.
 * Since we are using bufferevent and its deferred callback option,
 * the bufferevent output buffer may still contain pending data when the [serverWriteCallback] is called.
 * To handle this, we check whether the output buffer is empty or not. If all of these conditions are met, we drop connection.
 * Otherwise, we call session_send() to process the pending output data.
 * Remember that in send_callback(), we must not write all data to bufferevent to avoid excessive buffering.
 * We continue processing pending data when the output buffer becomes empty.
 */
internal fun serverWriteCallback(
    bufferEvent: CPointer<bufferevent>?,
    ptr: COpaquePointer
) {
    val sessionData = ptr.asStableRef<HttpSessionData>().get()
    if (evbuffer_get_length(bufferevent_get_output(bufferEvent)) > 0u) return
    if (nghttp2_session_want_read(sessionData.httpSession) == 0 &&
        nghttp2_session_want_write(sessionData.httpSession) == 0
    ) {
        deleteHttpSessionData(sessionData)
        return
    }
    if (sessionSend(sessionData) != 0) {
        deleteHttpSessionData(sessionData)
    }
}

/**
 * Called when the reception of a header block in HEADERS or PUSH_PROMISE frame is started.
 */
internal fun onBeginHeadersCallback(
    session: CPointer<nghttp2_session>?,
    frame: CPointer<nghttp2_frame>?,
    userData: COpaquePointer
): Int {
    val sessionData = userData.asStableRef<HttpSessionData>().get()
    frame?.pointed?.let {
        if (it.hd.type.toUInt() != NGHTTP2_HEADERS ||
            it.headers.cat != NGHTTP2_HCAT_REQUEST) {
            return 0
        }
        val streamData: CValuesRef<*>? = create_http2_stream_data(sessionData, it.hd.stream_id)
        nghttp2_session_set_stream_user_data(session, it.hd.stream_id, streamData)
    }
    return 0
}

/**
 * Each header name/value pair is emitted via [onHeaderCallback] function, which is called after [onBeginHeadersCallback]
 * We search for the :path header field among the request headers and store the requested path
 * in the http2_stream_data object.
 * TODO Currently we ignore the :method header field and always treat the request as a GET request.
 */
internal fun onHeaderCallback(
    session: CPointer<nghttp2_session>?,
    frame: CPointer<nghttp2_frame>?,
    name: CPointer<UByteVar>?,
    nameLength: size_t,
    value: CPointer<UByteVar>?,
    valueLength: size_t,
    flags: uint8_t,
    userData: COpaquePointer
): Int {
    val path = ":path".cstr
    frame?.pointed?.let {
        if (it.hd.type.toUInt() == NGHTTP2_HEADERS) {
            if (it.headers.cat != NGHTTP2_HCAT_REQUEST) return 0
            val streamDataPtr = nghttp2_session_get_stream_user_data(session, it.hd.stream_id) ?: return 0
            val streamData = streamDataPtr.asStableRef<HttpStreamData>().get()
            streamData.requestPath ?: return 0
            if (nameLength.toInt() == path.size - 1 && memcmp(path, name, nameLength) == 0) {
                var j: size_t = 0.convert()
                while (j < valueLength && value?.get(j) != '?'.code.toUByte()) {
                    ++j
                }
                streamData.requestPath = percent_decode(value, j)
            }
        }
    }
    return 0
}

/**
 * Called when a frame is fully received.
 */
internal fun onFrameRecvCallback(
    session: CPointer<nghttp2_session>?,
    frame: CPointer<nghttp2_frame>?,
    userData: COpaquePointer
): Int {
    val sessionData = userData.asStableRef<HttpSessionData>().get()
    frame?.pointed?.let {
        when (it.hd.type.toUInt()) {
            NGHTTP2_DATA -> {
                //TODO
            }
            NGHTTP2_HEADERS -> {
                /* Check that the client request has finished */
                if ((it.hd.flags.toUInt() and NGHTTP2_FLAG_END_STREAM) != 0u) {
                    val streamDataPtr = nghttp2_session_get_stream_user_data(session, it.hd.stream_id) ?: return 0
                    return onRequestRecv(session, sessionData, streamDataPtr.asStableRef<HttpStreamData>().get())
                }
            }
        }
    }
    return 0
}

/**
 * Called when the stream is about to close.
 */
internal fun onStreamCloseCallback(
    session: CPointer<nghttp2_session>?,
    streamId: int32_t,
    errorCode: uint32_t,
    userData: COpaquePointer
): Int {
    val sessionData = userData.asStableRef<HttpSessionData>().get()
    val streamDataPtr = nghttp2_session_get_stream_user_data(session, streamId) ?: return 0
    val streamData = streamDataPtr.asStableRef<HttpStreamData>().get()
    remove_stream(sessionData, streamData)
    deleteHttpStreamData(streamData)
    return 0
}