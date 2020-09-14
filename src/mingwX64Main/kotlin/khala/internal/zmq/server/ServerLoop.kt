package khala.internal.zmq.server

import co.touchlab.stately.isolate.IsolateState
import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.ZmqSocket
import kotlin.native.concurrent.AtomicInt
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze


internal class ServerQueueState<L>(val queue: ArrayDeque<ServerLoopQuery<L>>, val pingSocket: ZmqSocket)

internal fun <L> createIsolateState(loopId: Int) = IsolateState {
    ServerQueueState(
        ArrayDeque<ServerLoopQuery<L>>(),
        ZmqContext.createAndBindDealer(
            "inproc://LOOP_WORKER_$loopId"
        )
    )
}

internal actual class ServerLoop<L> actual constructor(
    loopStateProducer: () -> L,
    backwardRouterBindAddress: String,
    backwardListener: BackwardListener<L>
)  {

    private val loopId = ZmqContext.loopCounter.addAndGet(1)

    private val loopWorker = Worker.start(name = "LOOP_WORKER_$loopId")

    private val isolatedQueue = createIsolateState<L>(loopId)

    init {
        isolatedQueue.access { } // wait until inproc socket binds
        loopWorker.execute(
            mode = TransferMode.SAFE,
            producer = {
                ServerLoopJobInitialState(
                    isolatedQueue, loopId,
                    loopStateProducer, backwardListener,
                    backwardRouterBindAddress
                ).freeze()
            },
            job = ::serverLoopJob
        )
    }

    actual fun invokeSafe(block: ServerLoopScope<L>.(L) -> Unit) {
        isolatedQueue.access {
            it.queue.addLast(ServerLoopQuery.InvokeQuery(block))
            val m = ZmqMsg()
            m.addString("")
            m.send(it.pingSocket)
        }
    }

    actual fun stopSafe() {
        isolatedQueue.access {
            it.queue.addLast(ServerLoopQuery.StopQuery())
            ZmqMsg().send(it.pingSocket)
        }
        isolatedQueue.dispose()
    }

}
