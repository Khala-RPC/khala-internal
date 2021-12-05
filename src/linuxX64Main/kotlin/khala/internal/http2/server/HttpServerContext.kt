package khala.internal.http2.server

import khala.internal.cinterop.openssl.SSL_CTX
import khala.internal.cinterop.libevent.event_base
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.StableRef

// struct app_context
// initialize_app_context
class HttpContext(
    val sslCtx: SSL_CTX?,
    val eventBase: CPointer<event_base>?
) {
    val stableRef = StableRef.create(this)
}