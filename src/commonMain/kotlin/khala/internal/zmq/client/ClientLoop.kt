package khala.internal.zmq.client

import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.ZmqSocket

/**
 * Thread-safe ZMQ client.
 */
internal expect class ClientLoop<L, S>(
    loopStateProducer: () -> L,
    socketStateProducer: (L) -> S,
    forwardListener: ClientLoopScope.(L, S, String, ZmqSocket, ZmqMsg) -> Unit
) {

    /**
     * Invokes [block] in the loop thread.
     * [block] will be frozen on K/N.
     */
    fun invokeSafe(
        block: ClientLoopScope.(L) -> Unit
    )

    /**
     * Sends a query to the loop to stop and close all managed sockets.
     */
    fun stopSafe()

}