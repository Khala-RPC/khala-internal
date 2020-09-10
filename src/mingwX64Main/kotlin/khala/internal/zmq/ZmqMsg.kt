package khala.internal.zmq

import khala.internal.cinterop.czmq.*
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.cValuesOf
import kotlinx.cinterop.toKString

actual class BinaryData @ExperimentalUnsignedTypes constructor(
    val rawData: COpaquePointer,
    val size: ULong
)

internal actual class ZmqMsg {

    private val message: CPointer<zmsg_t>

    actual constructor() {
        message = zmsg_new()!!
    }

    internal constructor(message: CPointer<zmsg_t>) {
        this.message = message
    }


    actual fun send(socket: ZmqSocket) {
        zmsg_send(cValuesOf(message), socket.socket)
    }

    actual fun addBytes(bytes: BinaryData) {
        zmsg_addmem(message, bytes.rawData, bytes.size)
    }

    actual fun addString(str: String) {
        zmsg_addstr(message, str)
    }

    @ExperimentalUnsignedTypes
    actual fun popBytes(): BinaryData {
        val frame = zmsg_pop(message)
        val data = zframe_data(frame)!!
        val size = zframe_size(frame)
        return BinaryData(data, size)
    }

    actual fun popString(): String {
        return zmsg_popstr(message)!!.toKString()
    }

    actual fun close() {
        zmsg_destroy(cValuesOf(message))
    }
}