package khala.internal.zmq

import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.server.LoopState
import khala.internal.zmq.server.ZmqLoop
import org.zeromq.ZMQ

internal class JobInitialState<S>(
    val loop: ZmqLoop<S>,
    val context: ZmqContext,
    val userStateProducer: () -> S,
    val forwardListener: LoopState<S>.(String, khala.internal.zmq.bindings.ZmqMsg) -> Unit,
    val backwardListener: LoopState<S>.(khala.internal.zmq.bindings.ZmqMsg) -> Unit,
    val backwardRouterBindAddress: String?
)

internal fun <S> loopJob(initialState: JobInitialState<S>) {
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
    val pongSocket = initialState.context.createAndConnectDealer(
            "inproc://LOOP_WORKER_${initialState.loop.loopId}"
    )
    with(loopState) {
        loop.poller.register(pongSocket.socket, { _, _ ->
            handlePongSocket(ZmqMsg.recv(pongSocket)!!) //TODO ZMQ error handling
            true
        }, ZMQ.Poller.POLLIN)
        if (backwardSocket != null) {
            loop.poller.register(backwardSocket.socket, { _, _ ->
                backwardListener(ZmqMsg.recv(backwardSocket)!!) // TODO ZMQ error handling
                true
            }, ZMQ.Poller.POLLIN)
        }
        while (!isStopped) { // TODO use ZLoop instead of poller
            loop.poller.poll(-1L)
        }
    }
}