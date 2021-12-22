package khala.internal.transport.http2.client

import cnames.structs.nghttp2_session_callbacks
import khala.internal.transport.cinterop.nghttp2.*
import khala.internal.transport.http2.server.*
import khala.internal.transport.http2.server.serverOnFrameRecvCallback
import khala.internal.transport.http2.server.serverOnHeaderCallback
import khala.internal.transport.http2.server.serverOnStreamCloseCallback
import kotlinx.cinterop.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

// initialize_nghttp2_session
internal fun initializeClientHttpSession(
    sessionData: HttpClientSessionData?
) {
    logger.info { "initializeClientHttpSession" }
    val callbacksPtr: CValuesRef<CPointerVar<nghttp2_session_callbacks>> = cValuesOf<nghttp2_session_callbacks>()
    nghttp2_session_callbacks_new(callbacksPtr)
    memScoped {
        val callbacks = callbacksPtr.getPointer(this)[0]
        nghttp2_session_callbacks_set_send_callback(callbacks, staticCFunction(::clientSendCallback))
        nghttp2_session_callbacks_set_on_frame_recv_callback(callbacks, staticCFunction(::clientOnFrameRecvCallback))
        nghttp2_session_callbacks_set_on_data_chunk_recv_callback(callbacks, staticCFunction(::clientOnDataChunkRecvCallback))
        nghttp2_session_callbacks_set_on_stream_close_callback(
            callbacks,
            staticCFunction(::clientOnStreamCloseCallback)
        )
        nghttp2_session_callbacks_set_on_header_callback(callbacks, staticCFunction(::clientOnHeaderCallback))
        nghttp2_session_callbacks_set_on_begin_headers_callback(
            callbacks,
            staticCFunction(::clientOnBeginHeadersCallback)
        )
        val arr = arrayOf(sessionData?.session)
        memScoped {
            nghttp2_session_client_new(arr.toCValues(), callbacks, sessionData?.stableRef?.asCPointer())
        }
        nghttp2_session_callbacks_del(callbacks)
    }
}

/**
 * The nghttp2_session_send() function serializes pending frames into wire format and calls the send_callback() function to send them.
 */
// session_send
internal fun clientSessionSend(sessionData: HttpClientSessionData?): Int {
    val rv = nghttp2_session_send(sessionData?.session)
    if (rv != 0) {
        logger.warn { "Fatal error: ${nghttp2_strerror(rv)}" }
        return -1
    }
    return 0
}