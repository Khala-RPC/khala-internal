package khala.internal.zmq

internal actual class ZmqLoop<S> actual constructor(
    private val context: ZmqContext,
    loopStateProducer: () -> S
) {

    private val sockets: MutableMap<String, ZmqSocket> = HashMap()
    private val loopState: S = loopStateProducer()

    private val varargWrapper = js("function(f) { return function() { return f(arguments); }; }")

    actual fun addUnsafe(
        address: String,
        socket: ZmqSocket,
        onNewMessage: (ZmqMsg, S) -> Unit
    ) {
        sockets[address] = socket
        socket.socket.on("message", varargWrapper { args -> onNewMessage(ZmqMsg(args), loopState) })
    }

    actual fun removeUnsafe(address: String) {
        sockets.remove(address)?.close()
    }

    actual fun getOrCreateUnsafe(address: String): ZmqSocket {
        val existing = sockets[address]
        if (existing != null) return existing
        val new = context.createAndConnectDealer(address)
        sockets[address] = new
        return new
    }

    actual fun invokeSafe(block: (S) -> Unit) {
        block(loopState)
    }

    actual fun stopSafe() {
        sockets.values.forEach { it.close() }
        sockets.clear()
    }

}