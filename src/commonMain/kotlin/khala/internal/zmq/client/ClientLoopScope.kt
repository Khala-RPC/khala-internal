package khala.internal.zmq.client

import khala.internal.zmq.bindings.MsgBuilder
import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.ZmqSocket
import khala.internal.zmq.bindings.buildMsg

/**
 * This class works as a receiver for lambdas that will be called from the loop, so its methods are not thread-safe.
 */
internal expect class ClientLoopScope<L, S> {

    fun sendMessage(address: String, msg: ZmqMsg)
    fun remove(address: String)

}

internal inline fun <L, S> ClientLoopScope<L, S>.sendForward(address: String, block: MsgBuilder.() -> Unit) {
    sendMessage(address, buildMsg(block))
}

internal class SocketWithState<S>(val socket: ZmqSocket, val state: S)