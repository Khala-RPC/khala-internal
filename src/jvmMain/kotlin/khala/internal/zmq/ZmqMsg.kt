package khala.internal.zmq

actual typealias BinaryData = ByteArray

internal actual class ZmqMsg {
    actual fun send(socket: ZmqSocket) {
    }

    actual fun addBytes(bytes: BinaryData) {
    }

    actual fun addString(str: String) {
    }

    actual fun popBytes(): BinaryData {
        TODO("Not yet implemented")
    }

    actual fun popString(): String {
        TODO("Not yet implemented")
    }

    actual fun close() {
    }
}