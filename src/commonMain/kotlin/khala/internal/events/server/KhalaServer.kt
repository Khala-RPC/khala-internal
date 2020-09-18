package khala.internal.events.server

import khala.internal.zmq.server.ServerLoop
import khala.internal.zmq.server.ServerLoopScope

/**
 * Internal Server API for Khala bindings.
 */
class KhalaServer(address: String, ZmqHighWaterMark: Int = -1) {

    private val serverLoop = ServerLoop( //TODO High water mark
        loopStateProducer = ::produceServerState,
        backwardRouterBindAddress = address,
        backwardListener = ServerLoopScope<ServerState>::backwardListener
    )

}