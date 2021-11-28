package khala.internal.http2.server

import khala.internal.cinterop.libevent.bufferevent_get_output
import khala.internal.cinterop.libevent.bufferevent_write
import khala.internal.cinterop.libevent.evbuffer_get_length
import khala.internal.cinterop.nghttp2.NGHTTP2_ERR_WOULDBLOCK
import khala.internal.cinterop.nghttp2.nghttp2_session
import kotlinx.cinterop.*
import platform.posix.size_t
import platform.posix.ssize_t

/**
 * The nghttp2_session_send() function serializes the frame into wire format and calls this function,
 * which is of type nghttp2_send_callback.
 */
internal fun serverSendCallback(
    session: CPointer<nghttp2_session>,
    data: CValuesRef<*>,
    length: size_t,
    flags: Int,
    userData: COpaquePointer
): ssize_t {
    val sessionData = userData.asStableRef<HttpSessionData>().get()
    val bufferEvent = sessionData.bufferEvent
    if (evbuffer_get_length(bufferevent_get_output(bufferEvent)) >= OUTPUT_WOULD_BLOCK_THRESHOLD) {
        return NGHTTP2_ERR_WOULDBLOCK.convert()
    }
    bufferevent_write(bufferEvent, data, length)
    return length.convert()
}