package khala.internal.zmq

internal actual class ZmqLoop<S> actual constructor(context: ZmqContext, loopStateProducer: () -> S) {
    actual fun addUnsafe(
        address: String,
        socket: ZmqSocket,
        onNewMessage: (ZmqMsg, S) -> Unit
    ) {
    }

    actual fun removeUnsafe(address: String) {
    }

    actual fun getOrCreateUnsafe(address: String): ZmqSocket {
        TODO("Not yet implemented")
    }

    actual fun invokeSafe(block: (S) -> Unit) {
    }

    actual fun stopSafe() {
    }
}