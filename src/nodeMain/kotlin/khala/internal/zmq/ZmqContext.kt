package khala.internal.zmq

internal val zmq = js("require('zeromq')")
internal val jszmq = js("require('@prodatalab/jszmq')")

internal class ZmqContext() {

    fun createAndConnectDealer(address: String): ZmqSocket {
        val isWebSocket = address.startsWith("ws")
        val socket = if (isWebSocket) jszmq.socket("dealer") else zmq.socket("dealer")
        val zmqSocket = ZmqSocket(socket, isWebSocket)
        zmqSocket.connect(address)
        return zmqSocket
    }

    fun createAndBindDealer(address: String): ZmqSocket {
        val isWebSocket = address.startsWith("ws")
        val socket = if (isWebSocket) jszmq.socket("dealer") else zmq.socket("dealer")
        val zmqSocket = ZmqSocket(socket, isWebSocket)
        zmqSocket.bind(address)
        return zmqSocket
    }

    fun createAndBindRouter(address: String): ZmqSocket {
        val isWebSocket = address.startsWith("ws")
        val socket = if (isWebSocket) jszmq.socket("router") else zmq.socket("router")
        val zmqSocket = ZmqSocket(socket, isWebSocket)
        zmqSocket.bind(address)
        return zmqSocket
    }

    fun close() { /* do nothing */ }

}