package khala.internal.zmq

internal val jszmq = js("require('@prodatalab/jszmq')")

internal actual class ZmqContext actual constructor() {

    actual fun createAndConnectDealer(address: String): ZmqSocket {
        val socket = jszmq.socket("dealer")
        val zmqSocket = ZmqSocket(socket)
        zmqSocket.connect(address)
        return zmqSocket
    }

    actual fun createAndBindDealer(address: String): ZmqSocket {
        val socket = jszmq.socket("dealer")
        val zmqSocket = ZmqSocket(socket)
        zmqSocket.bind(address)
        return zmqSocket
    }

    actual fun createAndBindRouter(address: String): ZmqSocket {
        val socket = jszmq.socket("router")
        val zmqSocket = ZmqSocket(socket)
        zmqSocket.bind(address)
        return zmqSocket
    }

    actual fun close() { /* do nothing */ }

}