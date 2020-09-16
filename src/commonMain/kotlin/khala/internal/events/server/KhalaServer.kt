package khala.internal.events.server

import khala.internal.zmq.server.ServerLoop
import khala.internal.zmq.server.ServerLoopScope

/**
 * Internal Server API for Khala bindings.
 */
class KhalaServer(address: String) {

    private val serverLoop = ServerLoop(
        loopStateProducer = ::produceServerState,
        backwardRouterBindAddress = address,
        backwardListener = ServerLoopScope<ServerState>::backwardListener
    )

}