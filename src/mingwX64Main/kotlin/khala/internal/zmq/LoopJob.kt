package khala.internal.zmq

import khala.internal.cinterop.zmq.ZMQ_POLLIN
import khala.internal.cinterop.zmq.zmq_poll
import khala.internal.cinterop.zmq.zmq_pollitem_t
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped

internal class JobInitialState<S>(
    val loop: ZmqLoop<S>,
    val context: ZmqContext,
    val userStateProducer: () -> S,
    val forwardListener: LoopState<S>.(String, ZmqMsg) -> Unit,
    val backwardListener: LoopState<S>.(ZmqMsg) -> Unit,
    val backwardRouterBindAddress: String?
)

internal fun <S> loopJob(initialState: JobInitialState<S>) {
    val pongSocket = initialState.context.createAndConnectDealer(
        "inproc://LOOP_WORKER_${initialState.loop.loopId}"
    )
    val backwardSocket = initialState.backwardRouterBindAddress?.let {
        initialState.context.createAndBindRouter(it)
    }
    val loopState = LoopState(
        loop = initialState.loop,
        context = initialState.context,
        forwardSockets = LinkedHashMap(),
        forwardListener = initialState.forwardListener,
        backwardSocket = backwardSocket,
        backwardListener = initialState.backwardListener,
        userState = initialState.userStateProducer()
    )
    with(loopState) {
        while (!isStopped) {
            val forwardSocketsList = forwardSockets.toList()
            val allSocketsList = arrayListOf(pongSocket)
            if (backwardSocket != null) allSocketsList += backwardSocket
            allSocketsList += forwardSocketsList.map { it.second }
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
                        when {
                            i == 0 ->
                                handlePongSocket(msg)
                            i == 1 && backwardSocket != null ->
                                this@with.backwardListener(msg)
                            backwardSocket != null ->
                                this@with.forwardListener(forwardSocketsList[i - 2].first, msg)
                            else ->
                                this@with.forwardListener(forwardSocketsList[i - 1].first, msg)
                        }
                    }
                }
            }
        }
    }
}