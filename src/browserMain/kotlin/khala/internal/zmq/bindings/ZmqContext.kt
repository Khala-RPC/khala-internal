package khala.internal.zmq.bindings

internal val jszmq = js("require('@prodatalab/jszmq')")

internal actual object ZmqContext {

    actual fun createAndConnectDealer(address: String): ZmqSocket {
        val socket = jszmq.socket("dealer")
        val zmqSocket = ZmqSocket(socket)
        zmqSocket.connect(address)
        return zmqSocket
    }

    /**
     * Binding sockets is NOT supported in browser.
     * This function works only in NodeJS environment,
     * for cases where this library is used in code shared between browser and node.
     */
    actual fun createAndBindDealer(address: String): ZmqSocket {
        val socket = jszmq.socket("dealer")
        val zmqSocket = ZmqSocket(socket)
        zmqSocket.bind(address)
        return zmqSocket
    }

    /**
     * Binding sockets is NOT supported in browser.
     * This function works only in NodeJS environment,
     * for cases where this library is used in code shared between browser and node.
     */
    actual fun createAndBindRouter(address: String): ZmqSocket {
        val socket = jszmq.socket("router")
        val zmqSocket = ZmqSocket(socket)
        zmqSocket.bind(address)
        return zmqSocket
    }

}