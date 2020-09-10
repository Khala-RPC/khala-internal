package khala.internal.zmq

import khala.internal.cinterop.zmq.*

internal actual class ZmqContext {

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

    actual fun close() {
        zmq_ctx_destroy(context)
    }
}