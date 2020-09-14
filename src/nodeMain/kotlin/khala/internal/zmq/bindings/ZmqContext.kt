package khala.internal.zmq.bindings

internal val zmq = js("require('zeromq')")
internal val jszmq = js("require('@prodatalab/jszmq')")

internal actual object ZmqContext {

    actual fun createAndConnectDealer(address: String): ZmqSocket {
        val isWebSocket = address.startsWith("ws")
        val socket = if (isWebSocket) jszmq.socket("dealer") else zmq.socket("dealer")
        val zmqSocket = ZmqSocket(socket, isWebSocket)
        zmqSocket.connect(address)
        return zmqSocket
    }

    actual fun createAndBindDealer(address: String): ZmqSocket {
        val isWebSocket = address.startsWith("ws")
        val socket = if (isWebSocket) jszmq.socket("dealer") else zmq.socket("dealer")
        val zmqSocket = ZmqSocket(socket, isWebSocket)
        zmqSocket.bind(address)
        return zmqSocket
    }

    actual fun createAndBindRouter(address: String): ZmqSocket {
        val isWebSocket = address.startsWith("ws")
        val socket = if (isWebSocket) jszmq.socket("router") else zmq.socket("router")
        val zmqSocket = ZmqSocket(socket, isWebSocket)
        zmqSocket.bind(address)
        return zmqSocket
    }

}