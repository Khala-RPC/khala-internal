package khala.internal.zmq

internal class ZmqLoop<S, L>(context: ZmqContext, loopStateProducer: () -> L) {

    val sockets: MutableMap<Int, ZmqSocket> = LinkedHashMap()

    fun addSafe(
        socket: ZmqSocket,
        socketStateProducer: () -> S,
        onNewMessage: (ZmqMsg, S, L) -> Unit
    ): Int {
        return 1
    }

    fun addUnsafe(
        socket: ZmqSocket,
        socketStateProducer: () -> S,
        onNewMessage: (ZmqMsg, S, L) -> Unit
    ): Int {
        return 1
    }

    fun removeSafe(socketID: Int) {

    }

    fun removeUnsafe(socketID: Int) {

    }

    fun start() {

    }

    fun stop() {

    }

}