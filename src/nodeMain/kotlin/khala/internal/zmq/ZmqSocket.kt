package khala.internal.zmq

internal class ZmqSocket(val socket: dynamic) {

    fun connect(address: String) {
        socket.connect(address)
    }

    fun bind(address: String) {
        socket.bindSync(address)
    }

    fun close() {
        socket.close()
    }
}