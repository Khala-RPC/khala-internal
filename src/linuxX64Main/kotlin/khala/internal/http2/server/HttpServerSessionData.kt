package khala.internal.http2.server

import khala.internal.cinterop.libevent.*
import khala.internal.cinterop.nghttp2.nghttp2_session
import khala.internal.cinterop.nghttp2.nghttp2_session_del
import kotlinx.cinterop.*
import kotlinx.cinterop.internal.CCall
import mu.KotlinLogging
import platform.linux.malloc_info
import platform.posix.IPPROTO_TCP
import platform.posix.TCP_NODELAY
import platform.posix.free
import platform.posix.strdup

private val logger = KotlinLogging.logger {}

// struct http2_session_data
class HttpSessionData(
    val root: HttpStreamData?,
    val bufferEvent: CPointer<bufferevent>?,
    val httpContext: HttpContext?,
    val httpSession: CPointer<nghttp2_session>?,
    val clientAddress: CPointer<ByteVar>?
) {
    val stableRef = StableRef.create(this)
}

// create_http2_session_data
internal fun createSessionData(
    httpContext: HttpContext?,
    fd: Int,
    address: CPointer<sockaddr>?,
    addressLength: Int
): HttpSessionData {
    val valValue = cValuesOf(1)
    val valRef: CValuesRef<*> = valValue
    //TODO SSL stuff (see https://nghttp2.org/documentation/tutorial-server.html)
    setsockopt(fd, IPPROTO_TCP, TCP_NODELAY, valRef, valValue.size.convert())
    val bufferEvent = bufferevent_socket_new(
        httpContext?.eventBase, fd, (BEV_OPT_CLOSE_ON_FREE or BEV_OPT_DEFER_CALLBACKS).toInt()
    )
    bufferevent_enable(bufferEvent, (EV_READ or EV_WRITE).toShort())
    val host = ByteArray(NI_MAXHOST)
    val rv = getnameinfo(address, addressLength.convert(), host.refTo(0), host.size.convert(), null, 0, NI_NUMERICHOST)
    val clientAddress = if (rv != 0) {
        strdup("(unknown)")
    } else {
        strdup(host.toKString())
    }
    return HttpSessionData(
        root = null,
        bufferEvent = bufferEvent,
        httpContext = httpContext,
        httpSession = null,
        clientAddress = clientAddress
    )
}

// delete_http2_session_data
internal fun deleteSessionData(sessionData: HttpSessionData) {
    logger.info { "Client disconnected: ${sessionData.clientAddress}" }
    //TODO SSL stuff (see https://nghttp2.org/documentation/tutorial-server.html)
    bufferevent_free(sessionData.bufferEvent)
    nghttp2_session_del(sessionData.httpSession)
}