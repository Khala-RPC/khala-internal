package khala.internal.zmq.bindings

import org.zeromq.ZMQ

internal actual class ZmqSocket(val socket: ZMQ.Socket) {

    fun connect(address: String) {
        socket.connect(address)
    }

    fun bind(address: String) {
        socket.bind(address)
    }

    actual fun close() {
        socket.close()
    }
}