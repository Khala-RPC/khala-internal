package khala.internal.zmq.bindings

import org.zeromq.SocketType
import org.zeromq.ZContext
import java.util.concurrent.atomic.AtomicInteger

internal actual object ZmqContext {

    internal val loopCounter = AtomicInteger(0)

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

}