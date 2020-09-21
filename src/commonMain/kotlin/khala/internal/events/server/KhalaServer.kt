package khala.internal.events.server

import khala.internal.zmq.server.ServerLoop
import khala.internal.zmq.server.ServerLoopScope
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Internal Server API for Khala bindings.
 */
@ExperimentalJsExport
@JsExport
class KhalaServer(address: String) {

    private val serverLoop = ServerLoop( //TODO High water mark
        loopStateProducer = ::produceServerState,
        backwardRouterBindAddress = address,
        backwardListener = ServerLoopScope<ServerState>::backwardListener
    )

}