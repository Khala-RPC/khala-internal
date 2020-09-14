package khala.internal.zmq.server

import khala.internal.zmq.bindings.ZmqMsg

/**
 * This class works as a receiver for lambdas that will be called from the loop, so its methods are not thread-safe.
 */
internal expect class ServerLoopScope {

    fun sendBackward(msg: ZmqMsg)

}