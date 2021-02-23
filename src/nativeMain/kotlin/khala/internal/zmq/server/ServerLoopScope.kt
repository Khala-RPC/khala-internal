package khala.internal.zmq.server

import co.touchlab.stately.isolate.IsolateState
import khala.internal.zmq.PriorityQueue
import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.ZmqSocket
import khala.internal.zmq.client.ClientLoopScope
import kotlin.system.getTimeMillis

internal actual class ServerLoopScope<L>(
    val isolatedQueue: IsolateState<ServerQueueState<L>>,
    val backwardSocket: ZmqSocket,
    val backwardListener: BackwardListener<L>,
    val loopState: L,
    var isStopped: Boolean
) {

    val scheduledBlocks = PriorityQueue<ServerScheduledBlock<L>>()

    actual fun sendMessage(msg: ZmqMsg) {
        msg.send(backwardSocket)
    }

    actual fun invokeAfterShortDelay(block: ServerLoopScope<L>.(L) -> Unit) {
        //TODO
        invokeAfterTimeout(5000, block)
    }

    actual fun invokeAfterLongDelay(block: ServerLoopScope<L>.(L) -> Unit) {
        //TODO
        invokeAfterTimeout(60000, block)
    }

    private fun invokeAfterTimeout(
        timeoutMillis: Long,
        block: ServerLoopScope<L>.(L) -> Unit
    ) {
        scheduledBlocks.add(
            ServerScheduledBlock(
                getTimeMillis() + timeoutMillis,
                block
            )
        )
    }

}