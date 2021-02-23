package khala.internal.zmq.client

import khala.internal.zmq.bindings.MsgBuilder
import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.ZmqSocket
import khala.internal.zmq.bindings.buildMsg

/**
 * This class works as a receiver for lambdas that will be called from the loop, so its methods are NOT thread-safe.
 */
internal expect class ClientLoopScope<L, S> {

    /**
     * Sends the message to [address], creating a connection if necessary.
     */
    fun sendMessage(address: String, msg: ZmqMsg)

    /**
     * Closes the connection to [address]
     */
    fun remove(address: String)

    /**
     * Invokes the lambda in event loop thread after short timeout (around 5 seconds).
     * Timeout is NOT exact, lambda may be called a lot later if the loop is high loaded.
     * Few seconds of delay are OK for this function.
     */
    fun invokeAfterShortDelay(block: ClientLoopScope<L, S>.(L) -> Unit)

    /**
     * Invokes the lambda in event loop thread after long timeout (around 1 minute).
     * Timeout is NOT exact, lambda may be called a lot later if the loop is high loaded.
     * Few seconds of delay are OK for this function.
     */
    fun invokeAfterLongDelay(block: ClientLoopScope<L, S>.(L) -> Unit)

}


internal inline fun <L, S> ClientLoopScope<L, S>.sendForward(address: String, block: MsgBuilder.() -> Unit) {
    sendMessage(address, buildMsg(block))
}

internal class SocketWithState<S>(val socket: ZmqSocket, val state: S)