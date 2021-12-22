package khala.internal.transport.http2.server

import khala.internal.transport.cinterop.libevent.*
import khala.internal.transport.cinterop.nghttp2.*
import kotlinx.cinterop.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Launches the HTTP/2 server
 */
// run
fun runServer(
    service: String,
    keyFile: String,
    certificateFile: String
) {
    //TODO SSL stuff (see https://nghttp2.org/documentation/tutorial-server.html)
    val eventBase = event_base_new()
    val appContext = HttpContext(null, eventBase)
    startServerListening(eventBase, service, appContext)
    event_base_loop(eventBase, 0)
    event_base_free(eventBase)
}

/**
 * Start accepting incoming connections
 */
// start_listen
internal fun startServerListening(
    eventBase: CPointer<event_base>?,
    service: String,
    httpContext: HttpContext
) = memScoped {
    logger.info { "startServerListening" }
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
    val values = allocArray<CPointerVar<addrinfo>>(1)
    val rv = getaddrinfo(null, service, hints.ptr, values)
    if (rv != 0) error("Can't resolve service address: $service")
    var rp: CPointer<addrinfo>? = values[0]
    while (rp != null) {
        val eventConnectionListener: CPointer<evconnlistener>? = evconnlistener_new_bind(
            eventBase,
            staticCFunction(::serverNewConnectionAcceptCallback),
            httpContext.stableRef.asCPointer(),
            LEV_OPT_CLOSE_ON_FREE or LEV_OPT_REUSEABLE,
            16,
            rp.pointed.ai_addr,
            rp.pointed.ai_addrlen.toInt()
        )
        if (eventConnectionListener != null) {
            freeaddrinfo(values[0])
            return@memScoped
        }
        rp = rp.pointed.ai_next
    }
    error("Could not start listener")
}

// send_server_connection_header
internal fun sendServerConnectionHeader(sessionData: HttpServerSessionData?): Int {
    val settingsEntry = cValue<nghttp2_settings_entry> {
        settings_id = NGHTTP2_SETTINGS_MAX_CONCURRENT_STREAMS.convert()
        value = 100u
    }
    val rv = nghttp2_submit_settings(sessionData?.httpSession, NGHTTP2_FLAG_NONE.convert(), settingsEntry, 1)
    if (rv != 0) {
        logger.warn { "Fatal error: ${nghttp2_strerror(rv)}" }
        return -1
    }
    return 0
}