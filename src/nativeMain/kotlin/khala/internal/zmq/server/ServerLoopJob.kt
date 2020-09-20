package khala.internal.zmq.server

import co.touchlab.stately.isolate.IsolateState
import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.poll
import kotlin.system.getTimeMillis

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
            val currentTime = getTimeMillis()
            while (scheduledBlocks.isNotEmpty()) {
                val scheduledBlockTime = scheduledBlocks.peek().scheduleTimeMillis
                if (scheduledBlockTime > currentTime) break
                val scheduledBlock = scheduledBlocks.poll()
                scheduledBlock.block(this, loopState)
            }
            val allSocketsList = arrayListOf(pongSocket, backwardSocket)
            //TODO use zloop because poller can read only 1 msg
            poll(allSocketsList) { i, msg ->
                if (i == 0) handlePongSocket(msg)
                else this@with.backwardListener(loopState, msg)
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