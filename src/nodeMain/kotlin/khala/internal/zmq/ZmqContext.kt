package khala.internal.zmq

internal val zmq = js("require('zeromq')")
internal val jszmq = js("require('jszmq')")


internal fun kotlinStrToJsStr(kotlinStr: String): dynamic {
    var s = js("''")
    kotlinStr.forEach {
            x -> s = s.concat(js("String.fromCharCode(x.c)"))
    }
    return s
}

internal class ZmqContext() {

    fun createAndConnectDealer(address: String): ZmqSocket {
        val isWebSocket = address.startsWith("ws")
        val socket = if (isWebSocket) jszmq.socket("dealer") else zmq.socket("dealer")
        console.log(socket)
        val zmqSocket = ZmqSocket(socket, isWebSocket)
        //zmqSocket.connect(kotlinStrToJsStr(address))
        return zmqSocket
    }

    fun createAndBindDealer(address: String): ZmqSocket {
        val isWebSocket = address.startsWith("ws")
        val socket = if (isWebSocket) jszmq.socket("dealer") else zmq.socket("dealer")
        console.log(socket)
        val zmqSocket = ZmqSocket(socket, isWebSocket)
        zmqSocket.bind(js("'ws://localhost:80'"))
        //zmqSocket.bind(kotlinStrToJsStr(address))
        return zmqSocket
    }

    fun createAndBindRouter(address: String): ZmqSocket {
        val isWebSocket = address.startsWith("ws")
        val socket = if (isWebSocket) jszmq.socket("router") else zmq.socket("router")
        val zmqSocket = ZmqSocket(socket, isWebSocket)
        zmqSocket.bind(kotlinStrToJsStr(address))
        return zmqSocket
    }

    fun close() { /* do nothing */ }

}