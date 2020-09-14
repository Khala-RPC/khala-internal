package khala.internal.zmq.bindings

import org.zeromq.SocketType
import org.zeromq.ZContext

internal actual class ZmqContext {

    internal val context = ZContext()

    actual fun createAndConnectDealer(address: String): ZmqSocket {
        val socket = ZmqSocket(context.createSocket(SocketType.DEALER))
        socket.connect(address)
        return socket
    }

    actual fun createAndBindDealer(address: String): ZmqSocket {
        val socket = ZmqSocket(context.createSocket(SocketType.DEALER))
        socket.bind(address)
        return socket
    }

    actual fun createAndBindRouter(address: String): ZmqSocket {
        val socket = ZmqSocket(context.createSocket(SocketType.ROUTER))
        socket.bind(address)
        return socket
    }

    actual fun close() {
        context.close()
    }
}