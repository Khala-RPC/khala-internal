package khala.internal.zmq

internal actual class ZmqContext {
    actual fun createAndConnectDealer(address: String): ZmqSocket {
        TODO("Not yet implemented")
    }

    actual fun createAndBindDealer(address: String): ZmqSocket {
        TODO("Not yet implemented")
    }

    actual fun createAndBindRouter(address: String): ZmqSocket {
        TODO("Not yet implemented")
    }

    actual fun close() {
    }
}