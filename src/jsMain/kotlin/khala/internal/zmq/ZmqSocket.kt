package khala.internal.zmq

internal actual class ZmqSocket(internal val socket: dynamic) {

    private val varargWrapper = js("function(sendFun) { return function(arr) { sendFun.apply(this, arr); }; }")
    internal val arraySender = varargWrapper(socket.send)

    internal fun connect(address: String) {
        if (isClosed) println("Socket is already closed!")
        else socket.connect(address)
    }

    private var bindFinished = true
    private val bindQueue = ArrayDeque<dynamic>()
    private var shouldClose = false
    private var isClosed = false

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
        socket.close()
    }
}