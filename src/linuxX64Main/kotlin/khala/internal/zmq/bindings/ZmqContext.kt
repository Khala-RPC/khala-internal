package khala.internal.zmq.bindings

import khala.internal.cinterop.zmq.*
import kotlin.native.concurrent.AtomicInt

internal actual object ZmqContext {

    internal val loopCounter = AtomicInt(0)

    private val context = zmq_ctx_new()

    actual fun createAndConnectDealer(address: String): ZmqSocket {
        val socket = ZmqSocket(zmq_socket(context, ZMQ_DEALER)!!)
        socket.connect(address)
        return socket
    }

    actual fun createAndBindDealer(address: String): ZmqSocket {
        val socket = ZmqSocket(zmq_socket(context, ZMQ_DEALER)!!)
        socket.bind(address)
        return socket
    }

    actual fun createAndBindRouter(address: String): ZmqSocket {
        val socket = ZmqSocket(zmq_socket(context, ZMQ_ROUTER)!!)
        socket.bind(address)
        return socket
    }

}