package khala.internal.zmq.server

import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.ZmqSocket

internal actual class ServerLoopScope<L>(
    val backwardSocket: ZmqSocket,
    val loopState: L,
    var isStopped: Boolean
) {

    actual fun sendMessage(msg: ZmqMsg) {
        msg.send(backwardSocket)
    }
}