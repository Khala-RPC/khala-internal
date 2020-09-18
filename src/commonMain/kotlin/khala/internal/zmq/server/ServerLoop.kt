package khala.internal.zmq.server

import khala.internal.zmq.bindings.ZmqMsg

internal typealias BackwardListener<L> =
        ServerLoopScope<L>.(loopState: L, msg: ZmqMsg) -> Unit

/**
 * Thread-safe ZMQ server. //TODO Make multiple bind addresses!!!!!!!!!
 */
internal expect class ServerLoop<L>(
    loopStateProducer: () -> L,
    backwardRouterBindAddress: String,
    backwardListener: BackwardListener<L>
) {

    /**
     * Invokes [block] in the loop thread.
     * [block] will be frozen on K/N.
     */
    fun invokeSafe(
        block: ServerLoopScope<L>.(L) -> Unit
    )

    /**
     * Sends a query to the loop to stop and close managed router socket.
     */
    fun stopSafe()

}