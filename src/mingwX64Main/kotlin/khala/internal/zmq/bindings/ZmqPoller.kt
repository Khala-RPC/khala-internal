package khala.internal.zmq.bindings

import khala.internal.cinterop.zmq.ZMQ_POLLIN
import khala.internal.cinterop.zmq.zmq_poll
import khala.internal.cinterop.zmq.zmq_pollitem_t
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped

internal actual fun poll(sockets: List<ZmqSocket>, onMsg: (idx: Int, msg: ZmqMsg) -> Unit) {
    memScoped {
        val pollItems = allocArray<zmq_pollitem_t>(sockets.size)
        for (i in sockets.indices) {
            pollItems[i].socket = sockets[i].socket
            pollItems[i].events = ZMQ_POLLIN.convert()
        }
        val rc = zmq_poll(pollItems, sockets.size, -1)
        // TODO return code check
        for (i in sockets.indices) {
            if (pollItems[i].revents == ZMQ_POLLIN.convert<Short>()) {
                val msg = ZmqMsg.recv(sockets[i])!! //TODO handle ZMQ error
                onMsg(i, msg)
            }
        }
    }
}