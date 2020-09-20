package khala.internal.zmq.client

import co.touchlab.stately.isolate.IsolateState
import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.poll
import kotlin.system.getTimeMillis

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
            val currentTime = getTimeMillis()
            while (scheduledBlocks.isNotEmpty()) {
                val scheduledBlockTime = scheduledBlocks.peek().scheduleTimeMillis
                if (scheduledBlockTime > currentTime) break
                val scheduledBlock = scheduledBlocks.poll()
                scheduledBlock.block(this, loopState)
            }
            val forwardSocketsList = forwardSockets.toList()
            val allSocketsList = arrayListOf(pongSocket)
            allSocketsList += forwardSocketsList.map { it.second.socket }
            //TODO use zloop because poller can read only 1 msg
            poll(allSocketsList) { i, msg ->
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

private fun <L, S> ClientLoopScope<L, S>.handlePongSocket(msg: ZmqMsg) {
    msg.close()
    val query: ClientLoopQuery<L, S> = isolatedQueue.access { it.queue.removeFirst() }
    when (query) {
        is ClientLoopQuery.InvokeQuery<L, S> -> query.block(this, loopState)
        is ClientLoopQuery.StopQuery<L, S> -> isStopped = true
    }
}