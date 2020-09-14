package khala.internal.zmq.server


import co.touchlab.stately.isolate.IsolateState
import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.bindings.ZmqMsg
import org.zeromq.ZMQ
import org.zeromq.ZPoller

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
    val poller = ZPoller(ZmqContext.context)
    val loopScope = ServerLoopScope(
        isolatedQueue = initialState.isolatedQueue,
        poller = poller,
        backwardSocket = backwardSocket,
        backwardListener = initialState.backwardListener,
        loopState = initialState.loopStateProducer(),
        isStopped = false
    )
    with(loopScope) {
        poller.register(pongSocket.socket, { _, _ ->
            handlePongSocket(ZmqMsg.recv(pongSocket)!!) //TODO ZMQ error handling
            true
        }, ZMQ.Poller.POLLIN)
        poller.register(backwardSocket.socket, { _, _ ->
            backwardListener(loopState, ZmqMsg.recv(backwardSocket)!!) // TODO ZMQ error handling
            true
        }, ZMQ.Poller.POLLIN)
        while (!isStopped) { // TODO use ZLoop instead of poller
            poller.poll(-1L)
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