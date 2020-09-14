package khala.internal.zmq.client

import co.touchlab.stately.isolate.IsolateState
import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.bindings.ZmqMsg

internal actual class ClientLoopScope<L, S>(
    val isolatedQueue: IsolateState<QueueState<L, S>>,
    val forwardSockets: MutableMap<String, SocketWithState<S>>,
    val forwardListener: ForwardListener<L, S>,
    val socketStateProducer: (L) -> S,
    val loopState: L,
    var isStopped: Boolean
) {

    actual fun sendMessage(address: String, msg: ZmqMsg) {
        val socket = forwardSockets[address]?.socket ?: run {
            val newSocket = ZmqContext.createAndConnectDealer(address)
            forwardSockets[address] = SocketWithState(newSocket, socketStateProducer(loopState))
            newSocket
        }
        msg.send(socket)
    }

    actual fun remove(address: String) {
        forwardSockets.remove(address)?.socket?.close()
    }

}