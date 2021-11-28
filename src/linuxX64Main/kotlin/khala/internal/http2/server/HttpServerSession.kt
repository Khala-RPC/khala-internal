package khala.internal.http2.server

import khala.internal.cinterop.libevent.*
import khala.internal.cinterop.nghttp2.*
import khala.internal.cinterop.nghttp2.ssize_t
import kotlinx.cinterop.*
import mu.KotlinLogging
import platform.posix.size_t

private val logger = KotlinLogging.logger {}

internal fun initializeHttpSession(sessionData: HttpSessionData) {
    val callbacks: CPointerVar<nghttp2_session_callbacks> = CPointerVar(nativeNullPtr)
    nghttp2_session_callbacks_new(callbacks.ptr)
    nghttp2_session_callbacks_set_send_callback(callbacks.value, )
}

internal fun deleteHttpSessionData(sessionData: HttpSessionData) {
    //TODO
}

internal fun deleteHttpStreamData(streamData: HttpStreamData) {
    //TODO
}

/**
 * Sends pending outgoing frames.
 */
internal fun sessionSend(sessionData: HttpSessionData): Int {
    val rv = nghttp2_session_send(sessionData.httpSession)
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
 * To send any pending outgoing frames, we immediately call [sessionSend].
 */
internal fun sessionRecv(sessionData: HttpSessionData): Int {
    val input: CPointer<evbuffer>? = bufferevent_get_input(sessionData.bufferEvent)
    val dataLength: size_t = evbuffer_get_length(input)
    val data: CPointer<UByteVar>? = evbuffer_pullup(input, -1)
    val readLength: ssize_t = nghttp2_session_mem_recv(sessionData.httpSession, data, dataLength)
    if (readLength < 0) {
        logger.warn { "Fatal error during sessionRecv: ${nghttp2_strerror(readLength.toInt())}" }
        return -1
    }
    if (evbuffer_drain(input, readLength.convert()) != 0) {
        logger.warn { "Fatal error: evbuffer_drain failed" }
        return -1
    }
    if (sessionSend(sessionData) != 0) {
        return -1
    }
    return 0
}