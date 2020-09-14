package khala.internal.zmq.client

import co.touchlab.stately.isolate.IsolateState
import khala.internal.cinterop.zmq.ZMQ_POLLIN
import khala.internal.cinterop.zmq.zmq_poll
import khala.internal.cinterop.zmq.zmq_pollitem_t
import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.bindings.ZmqMsg
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped

internal class ClientLoopJobInitialState<L, S>(
    val isolatedQueue: IsolateState<ClientQueueState<L, S>>,
    val loopId: Int,
    val loopStateProducer: () -> L,
    val socketStateProducer: (L) -> S,
    val forwardListener: ForwardListener<L, S>
)

internal fun <L, S> clientLoopJob(initialState: ClientLoopJobInitialState<L, S>) {
    val pongSocket = ZmqContext.createAndConnectDealer(
        "inproc://LOOP_WORKER_${initialState.loopId}"
    )
    val loopScope = ClientLoopScope(
        isolatedQueue = initialState.isolatedQueue,
        forwardSockets = LinkedHashMap(),
        forwardListener = initialState.forwardListener,
        socketStateProducer = initialState.socketStateProducer,
        loopState = initialState.loopStateProducer(),
        isStopped = false
    )
    with(loopScope) {
        while (!isStopped) {
            val forwardSocketsList = forwardSockets.toList()
            val allSocketsList = arrayListOf(pongSocket)
            allSocketsList += forwardSocketsList.map { it.second.socket }
            //TODO use zloop because poller can read only 1 msg
            memScoped {
                val pollItems = allocArray<zmq_pollitem_t>(allSocketsList.size)
                for (i in allSocketsList.indices) {
                    pollItems[i].socket = allSocketsList[i].socket
                    pollItems[i].events = khala.internal.cinterop.czmq.ZMQ_POLLIN.convert()
                }
                val rc = zmq_poll(pollItems, allSocketsList.size, -1)
                // TODO return code check
                for (i in allSocketsList.indices) {
                    if (pollItems[i].revents == ZMQ_POLLIN.convert<Short>()) {
                        val msg = ZmqMsg.recv(allSocketsList[i])!! //TODO handle ZMQ error
                        if (i == 0) handlePongSocket(msg)
                        else this@with.forwardListener(
                            loopState,
                            forwardSocketsList[i - 1].second.state,
                            forwardSocketsList[i - 1].first,
                            forwardSocketsList[i - 1].second.socket,
                            msg
                        )
                    }
                }
            }
        }
    }
}

private fun <L, S> ClientLoopScope<L, S>.handlePongSocket(msg: ZmqMsg) {
    msg.close()
    val query: ClientLoopQuery<L, S> = isolatedQueue.access { it.queue.removeFirst() }
    when (query) {
        is ClientLoopQuery.InvokeQuery<L, S> -> query.block(this, loopState)
        is ClientLoopQuery.StopQuery<L, S> -> isStopped = true
    }
}