package khala.internal.zmq

import khala.internal.cinterop.czmq.*
import khala.internal.zmq.bindings.BinaryData
import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.ZmqSocket
import kotlinx.cinterop.*

actual class BinaryData constructor(
    val rawData: COpaquePointer,
    val size: Int
)

internal actual class ZmqMsg {

    companion object {

        fun recv(source: ZmqSocket): ZmqMsg? =
            zmsg_recv(source.socket)?.let { khala.internal.zmq.bindings.ZmqMsg(it) }

    }

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
        zmsg_addmem(message, bytes.rawData, bytes.size.convert())
    }

    actual fun addString(str: String) {
        zmsg_addstr(message, str)
    }

    actual fun popBytes(): BinaryData {
        val frame = zmsg_pop(message)
        val data = zframe_data(frame)!!
        val size = zframe_size(frame).convert<Int>()
        return khala.internal.zmq.bindings.BinaryData(data, size)
    }

    actual fun popString(): String {
        return zmsg_popstr(message)!!.toKString()
    }

    actual fun close() {
        zmsg_destroy(cValuesOf(message))
    }
}