package khala.internal.zmq.server

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

internal class ServerLoopJobInitialState<L>(
    val isolatedQueue: IsolateState<ServerQueueState<L>>,
    val loopId: Int,
    val loopStateProducer: () -> L,
    val backwardListener: BackwardListener<L>,
    val backwardRouterBindAddress: String
)

internal fun <L> serverLoopJob(initialState: ServerLoopJobInitialState<L>) {
    val pongSocket = ZmqContext.createAndConnectDealer(
        "inproc://LOOP_WORKER_${initialState.loopId}"
    )
    val backwardSocket = ZmqContext.createAndBindRouter(initialState.backwardRouterBindAddress)
    val loopScope = ServerLoopScope(
        isolatedQueue = initialState.isolatedQueue,
        backwardSocket = backwardSocket,
        backwardListener = initialState.backwardListener,
        loopState = initialState.loopStateProducer(),
        isStopped = false
    )
    with(loopScope) {
        while (!isStopped) {
            val allSocketsList = arrayListOf(pongSocket, backwardSocket)
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
                        else this@with.backwardListener(loopState, msg)
                    }
                }
            }
        }
    }
}

private fun <L> ServerLoopScope<L>.handlePongSocket(msg: ZmqMsg) {
    msg.close()
    val query: ServerLoopQuery<L> = isolatedQueue.access { it.queue.removeFirst() }
    when (query) {
        is ServerLoopQuery.InvokeQuery<L> -> query.block(this, loopState)
        is ServerLoopQuery.StopQuery<L> -> isStopped = true
    }
}