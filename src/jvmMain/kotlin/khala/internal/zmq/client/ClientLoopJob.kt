package khala.internal.zmq.client

import co.touchlab.stately.isolate.IsolateState
import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.bindings.ZmqMsg
import org.zeromq.ZMQ
import org.zeromq.ZPoller

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
        poller = ZPoller(ZmqContext.context),
        forwardSockets = LinkedHashMap(),
        forwardListener = initialState.forwardListener,
        socketStateProducer = initialState.socketStateProducer,
        loopState = initialState.loopStateProducer(),
        isStopped = false
    )
    with(loopScope) {
        poller.register(pongSocket.socket, { _, _ ->
            handlePongSocket(ZmqMsg.recv(pongSocket)!!) //TODO ZMQ error handling
            true
        }, ZMQ.Poller.POLLIN)
        while (!isStopped) { // TODO use ZLoop instead of poller
            poller.poll(-1L)
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