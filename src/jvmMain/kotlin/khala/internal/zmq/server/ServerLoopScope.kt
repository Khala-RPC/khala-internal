package khala.internal.zmq.server

import co.touchlab.stately.isolate.IsolateState
import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.ZmqSocket
import org.zeromq.ZPoller

internal actual class ServerLoopScope<L>(
    val isolatedQueue: IsolateState<ServerQueueState<L>>,
    val poller: ZPoller,
    val backwardSocket: ZmqSocket,
    val backwardListener: BackwardListener<L>,
    val loopState: L,
    var isStopped: Boolean
) {

    actual fun sendMessage(msg: ZmqMsg) {
        msg.send(backwardSocket)
    }

}