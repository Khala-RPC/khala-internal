package khala.internal.zmq

import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.server.LoopState

internal fun <S> LoopState<S>.handlePongSocket(msg: ZmqMsg) {
    msg.close()
    val query: LoopQuery<S> = loop.isolatedQueue.access { it.queue.removeFirst() }
    when (query) {
        is InvokeQuery<S> -> query.block(this)
        is StopQuery<S> -> isStopped = true
    }
}