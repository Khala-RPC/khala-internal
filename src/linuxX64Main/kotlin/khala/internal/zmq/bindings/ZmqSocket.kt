package khala.internal.zmq.bindings

import khala.internal.cinterop.zmq.zmq_bind
import khala.internal.cinterop.zmq.zmq_close
import khala.internal.cinterop.zmq.zmq_connect
import kotlinx.cinterop.COpaquePointer

internal actual class ZmqSocket(val socket: COpaquePointer) {

    fun connect(address: String) {
        zmq_connect(socket, address)
    }

    fun bind(address: String) {
        zmq_bind(socket, address)
    }

    actual fun close() {
        zmq_close(socket)
    }
}