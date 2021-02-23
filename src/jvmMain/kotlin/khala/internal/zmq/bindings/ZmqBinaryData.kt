package khala.internal.zmq.bindings

actual typealias ZmqBinaryData = ByteArray

internal actual fun ZmqBinaryData.toByteArray(): ByteArray = this

internal actual fun ByteArray.toZmqBinaryData(): ZmqBinaryData = this