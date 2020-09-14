package khala.internal.zmq.bindings

internal val jszmq = js("require('@prodatalab/jszmq')")

internal actual object ZmqContext {

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

}