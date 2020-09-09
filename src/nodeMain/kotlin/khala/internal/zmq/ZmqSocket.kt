package khala.internal.zmq

internal val http = js("require('https')")

internal class ZmqSocket(val socket: dynamic, val webSocket: Boolean) {

    fun connect(address: String) {
        if (isClosed) println("Socket is already closed!")
        else socket.connect(address)
    }

    var bindFinished = true
    val bindQueue = ArrayDeque<dynamic>()
    var shouldClose = false
    var isClosed = false
    val s = http.createServer()

    fun bind(address: String) {
        if (isClosed) println("Socket is already closed!")
        else if (!bindFinished) bindQueue.add(address)
        else if (!webSocket) {
            bindFinished = false
            socket.bind(address) { err ->
                bindFinished = true
                if (err != undefined) {
                    println(err)
                }
                if (bindQueue.isNotEmpty()) {
                    bind(bindQueue.removeFirst())
                } else if (shouldClose) {
                    close()
                }
            }
        }
        else {
            bindFinished = false
            socket.bind(address)
            bindFinished = true
        }
    }

    fun close() {
        if (isClosed) println("Socket is already closed!")
        else if (!bindFinished) shouldClose = true
        else {
            shouldClose = false
            try {
                socket.close()
            }
            catch (ex: Throwable) {
                // TODO Properly handle early close for jszmq WS sockets (it works for zeromq.js TCP sockets already)
                println(ex)
            }
        }
    }
}