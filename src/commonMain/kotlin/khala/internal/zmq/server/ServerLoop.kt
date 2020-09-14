package khala.internal.zmq.server

import khala.internal.zmq.bindings.ZmqMsg

/**
 * Thread-safe ZMQ server.
 */
internal expect class ServerLoop<L>(
    loopStateProducer: () -> L,
    backwardListener: ServerLoopScope.(L, ZmqMsg) -> Unit,
    backwardRouterBindAddress: String
) {

    /**
     * Invokes [block] in the loop thread.
     * [block] will be frozen on K/N.
     */
    fun invokeSafe(
        block: ServerLoopScope.(L) -> Unit
    )

    /**
     * Sends a query to the loop to stop and close managed router socket.
     */
    fun stopSafe()

}