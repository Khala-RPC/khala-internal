package khala.internal.zmq.client

import khala.internal.zmq.bindings.ZmqMsg

/**
 * This class works as a receiver for lambdas that will be called from the loop, so its methods are not thread-safe.
 */
internal expect class ClientLoopScope {

    fun sendForward(address: String, msg: ZmqMsg)
    fun remove(address: String)

}

