package khala.internal.zmq.server

import khala.internal.zmq.bindings.MsgBuilder
import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.buildMsg

/**
 * This class works as a receiver for lambdas that will be called from the loop, so its methods are not thread-safe.
 */
internal expect class ServerLoopScope {

    fun sendMessage(msg: ZmqMsg)

}

internal inline fun ServerLoopScope.sendBackward(block: MsgBuilder.() -> Unit) {
    sendMessage(buildMsg(block))
}