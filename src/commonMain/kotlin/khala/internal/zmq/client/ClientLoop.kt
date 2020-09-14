package khala.internal.zmq.client

import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.ZmqSocket

internal typealias ForwardListener<L, S> =
        ClientLoopScope<L, S>.(loopState: L, socketState: S, address: String, forwardSocket: ZmqSocket, msg: ZmqMsg) -> Unit

/**
 * Thread-safe ZMQ client.
 */
internal expect class ClientLoop<L, S>(
    loopStateProducer: () -> L,
    socketStateProducer: (L) -> S,
    forwardListener: ForwardListener<L, S>
) {

    /**
     * Invokes [block] in the loop thread.
     * [block] will be frozen on K/N.
     */
    fun invokeSafe(block: ClientLoopScope<L, S>.(L) -> Unit)

    /**
     * Sends a query to the loop to stop and close all managed sockets.
     */
    fun stopSafe()

}