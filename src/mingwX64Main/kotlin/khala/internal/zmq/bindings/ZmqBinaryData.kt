package khala.internal.zmq.bindings

import kotlinx.cinterop.*

actual class ZmqBinaryData constructor(
    val rawData: COpaquePointer,
    val size: Int
)

internal actual fun ZmqBinaryData.toByteArray(): ByteArray {
    TODO()
}

internal actual fun ByteArray.toZmqBinaryData(): ZmqBinaryData {
    TODO()
}