package khala.internal.http2.server

import khala.internal.cinterop.libevent.*
import kotlinx.cinterop.*

/**
 * Start accepting incoming connections
 */
fun startServerListening(
    eventBase: CPointer<event_base>,
    service: String,
    httpContext: HttpContext
) {
    val hints = cValue<addrinfo> {
        ai_addr = null
        ai_addrlen = 0.toUInt()
        ai_canonname = null
        ai_family = AF_UNSPEC
        ai_flags = AI_PASSIVE or AI_ADDRCONFIG
        ai_next = null
        ai_protocol = 0
        ai_socktype = SOCK_STREAM
    }
    val res: CPointerVar<addrinfo> = CPointerVar(nativeNullPtr)
    val rv = memScoped { getaddrinfo(null, service, hints.ptr, res.ptr) }
    if (rv != 0) error("Can't resolve service address: $service")
    var rp: CPointer<addrinfo>? = res.value
    while (rp != null) {
        val eventConnectionListener: CPointer<evconnlistener>? = evconnlistener_new_bind(
            eventBase,
            acceptCallback,
            httpContext.stableRef.asCPointer(),
            LEV_OPT_CLOSE_ON_FREE or LEV_OPT_REUSEABLE,
            16,
            rp.pointed.ai_addr,
            rp.pointed.ai_addrlen.toInt()
        )
        if (eventConnectionListener != null) {
            freeaddrinfo(res.value)
            return
        }
        rp = rp.pointed.ai_next
    }
    error("Could not start listener")
}

internal fun sendServerConnectionHeader(sessionData: HttpSessionData): Int {

}