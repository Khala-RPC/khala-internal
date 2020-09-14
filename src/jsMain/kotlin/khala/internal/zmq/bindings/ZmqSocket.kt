package khala.internal.zmq.bindings

internal actual class ZmqSocket(internal val socket: dynamic) {

    internal fun connect(address: String) {
        if (isClosed) println("Socket is already closed!")
        else socket.connect(address)
    }

    private var bindFinished = true
    private val bindQueue = ArrayDeque<dynamic>()
    private var shouldClose = false
    private var isClosed = false

    // Port 80 doesn't work for WebSocket (idk why)
    internal fun bind(address: String) {
        if (isClosed) println("Socket is already closed!")
        else if (!bindFinished) bindQueue.add(address)
        else {
            bindFinished = false
            socket.bind(address)
            bindFinished = true
        }
    }

    actual fun close() {
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