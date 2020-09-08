package khala.internal.zmq

internal class ZmqSocket(val socket: dynamic) {

    fun connect(address: String) {
        if (isClosed) println("Socket is already closed!")
        else socket.connect(address)
    }

    var bindFinished = true
    val bindQueue = ArrayDeque<String>()
    var shouldClose = false
    var isClosed = false

    fun bind(address: String) {
        if (isClosed) println("Socket is already closed!")
        else if (!bindFinished) bindQueue.add(address)
        else {
            bindFinished = false
            socket.bind(address) { err ->
                bindFinished = true
                if (err != undefined) {
                    println(err)
                }
                if (bindQueue.isNotEmpty()) {
                    bind(bindQueue.removeFirst())
                }
                else if (shouldClose) {
                    close()
                }
            }
        }
    }

    fun close() {
        if (isClosed) println("Socket is already closed!")
        else if (!bindFinished) shouldClose = true
        else {
            shouldClose = false
            socket.close()
        }
    }
}