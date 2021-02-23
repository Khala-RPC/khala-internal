package khala.internal.zmq.server

import khala.internal.zmq.bindings.MsgBuilder
import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.buildMsg
import khala.internal.zmq.client.ClientLoopScope

/**
 * This class works as a receiver for lambdas that will be called from the loop, so its methods are not thread-safe.
 */
internal expect class ServerLoopScope<L> {

    /**
     * Sends the message to the client, using the first frame of the message as an identity.
     */
    fun sendMessage(msg: ZmqMsg)

    /**
     * Invokes the lambda in event loop thread after short timeout (around 5 seconds).
     * Timeout is NOT exact, lambda may be called a lot later if the loop is high loaded.
     * Few seconds of delay are OK for this function.
     */
    fun invokeAfterShortDelay(block: ServerLoopScope<L>.(L) -> Unit)

    /**
     * Invokes the lambda in event loop thread after long timeout (around 1 minute).
     * Timeout is NOT exact, lambda may be called a lot later if the loop is high loaded.
     * Few seconds of delay are OK for this function.
     */
    fun invokeAfterLongDelay(block: ServerLoopScope<L>.(L) -> Unit)

}

internal inline fun <L> ServerLoopScope<L>.sendBackward(block: MsgBuilder.() -> Unit) {
    sendMessage(buildMsg(block))
}