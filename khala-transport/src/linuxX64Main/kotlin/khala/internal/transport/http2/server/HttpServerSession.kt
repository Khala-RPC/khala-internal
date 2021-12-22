package khala.internal.transport.http2.server

import khala.internal.transport.cinterop.libevent.*
import khala.internal.transport.cinterop.nghttp2.*
import khala.internal.transport.cinterop.nghttp2.ssize_t
import kotlinx.cinterop.*
import mu.KotlinLogging
import platform.posix.size_t

private val logger = KotlinLogging.logger {}

// initialize_nghttp2_session
internal fun initializeServerHttpSession(sessionData: HttpServerSessionData?) {
    logger.info { "initializeHttpSession" }
    val callbacksPtr: CValuesRef<CPointerVar<nghttp2_session_callbacks>> = cValuesOf<nghttp2_session_callbacks>()
    nghttp2_session_callbacks_new(callbacksPtr)
    memScoped {
        val callbacks = callbacksPtr.getPointer(this)[0]
        nghttp2_session_callbacks_set_send_callback(callbacks, staticCFunction(::serverSendCallback))
        nghttp2_session_callbacks_set_on_frame_recv_callback(callbacks, staticCFunction(::serverOnFrameRecvCallback))
        nghttp2_session_callbacks_set_on_stream_close_callback(
            callbacks,
            staticCFunction(::serverOnStreamCloseCallback)
        )
        nghttp2_session_callbacks_set_on_header_callback(callbacks, staticCFunction(::serverOnHeaderCallback))
        nghttp2_session_callbacks_set_on_begin_headers_callback(
            callbacks,
            staticCFunction(::serverOnBeginHeadersCallback)
        )
        val arr = arrayOf(sessionData?.httpSession)
        memScoped {
            nghttp2_session_server_new(arr.toCValues(), callbacks, sessionData?.stableRef?.asCPointer())
        }
        nghttp2_session_callbacks_del(callbacks)
    }
}

internal fun deleteHttpSessionData(sessionData: HttpServerSessionData?) {
    logger.info { "deleteHttpSession" }
    //TODO
}

/**
 * Sends pending outgoing frames.
 */
// session_send
internal fun serverSessionSend(sessionData: HttpServerSessionData?): Int {
    val rv = nghttp2_session_send(sessionData?.httpSession)
    if (rv != 0) {
        logger.warn { "Fatal error during sessionSend: ${nghttp2_strerror(rv)}" }
        return -1
    }
    return 0
}

/**
 * In this function, we feed all unprocessed but already received data to the nghttp2 session object
 * using the nghttp2_session_mem_recv() function.
 * The nghttp2_session_mem_recv() function processes the data and may both invoke the
 * previously setup callbacks and also queue outgoing frames.
 * To send any pending outgoing frames, we immediately call [serverSessionSend].
 */
// session_recv
internal fun serverSessionRecv(sessionData: HttpServerSessionData?): Int {
    val input: CPointer<evbuffer>? = bufferevent_get_input(sessionData?.bufferEvent)
    val dataLength: size_t = evbuffer_get_length(input)
    val data: CPointer<UByteVar>? = evbuffer_pullup(input, -1)
    val readLength: ssize_t = nghttp2_session_mem_recv(sessionData?.httpSession, data, dataLength)
    if (readLength < 0) {
        logger.warn { "Fatal error during sessionRecv: ${nghttp2_strerror(readLength.toInt())}" }
        return -1
    }
    if (evbuffer_drain(input, readLength.convert()) != 0) {
        logger.warn { "Fatal error: evbuffer_drain failed" }
        return -1
    }
    if (serverSessionSend(sessionData) != 0) {
        return -1
    }
    return 0
}