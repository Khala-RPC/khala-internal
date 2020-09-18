package khala.internal.zmq.client

import co.touchlab.stately.isolate.IsolateState
import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.bindings.ZmqMsg
import org.zeromq.ZPoller
import java.util.*

internal actual class ClientLoopScope<L, S>(
    val isolatedQueue: IsolateState<ClientQueueState<L, S>>,
    val poller: ZPoller,
    val forwardSockets: MutableMap<String, SocketWithState<S>>,
    val forwardListener: ForwardListener<L, S>,
    val socketStateProducer: (L) -> S,
    val loopState: L,
    var isStopped: Boolean
) {

    val scheduledBlocks = PriorityQueue<ClientScheduledBlock<L, S>>()

    actual fun sendMessage(address: String, msg: ZmqMsg) {
        val socket = forwardSockets[address]?.socket ?: run {
            val newSocket = ZmqContext.createAndConnectDealer(address)
            val newSocketState = socketStateProducer(loopState)
            forwardSockets[address] = SocketWithState(newSocket, newSocketState)
            poller.register(newSocket.socket, { _, _ ->
                this.forwardListener(loopState, newSocketState, address, newSocket, ZmqMsg.recv(newSocket)!!)
                true
            }, ZPoller.POLLIN)
            newSocket
        }
        msg.send(socket)
    }

    actual fun remove(address: String) {
        forwardSockets.remove(address)?.let {
            poller.unregister(it.socket)
            it.socket.close()
        }
    }

    actual fun invokeAfterTimeout(
        timeoutMillis: Long,
        block: ClientLoopScope<L, S>.(L) -> Unit
    ) {
        scheduledBlocks.add(ClientScheduledBlock(
            System.currentTimeMillis() + timeoutMillis,
            block
        ))
    }

}