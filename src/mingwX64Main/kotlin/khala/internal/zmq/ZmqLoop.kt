package khala.internal.zmq

import co.touchlab.stately.isolate.IsolateState
import kotlinx.cinterop.pin
import kotlinx.cinterop.staticCFunction
import kotlin.native.concurrent.AtomicInt
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze

private val loopCounter = AtomicInt(0)

internal class QueueState<S>(val queue: ArrayDeque<LoopQuery<S>>, val pingSocket: ZmqSocket)

internal actual class ZmqLoop<S> actual constructor(
    context: ZmqContext,
    userStateProducer: () -> S,
    forwardListener: LoopState<S>.(String, ZmqMsg) -> Unit,
    backwardListener: LoopState<S>.(ZmqMsg) -> Unit,
    backwardRouterBindAddress: String?
)  {

    internal val loopId = loopCounter.addAndGet(1)

    internal val isolatedQueue = IsolateState {
        QueueState(
            ArrayDeque<LoopQuery<S>>(),
            context.createAndBindDealer("inproc://LOOP_WORKER_$loopId")
        )
    }

    private val loopWorker = Worker.start(name = "LOOP_WORKER_$loopId")

    init {
        isolatedQueue.access { } // wait until inproc socket binds
        loopWorker.execute(
            mode = TransferMode.SAFE,
            producer = {
                JobInitialState(
                    this, context, userStateProducer,
                    forwardListener, backwardListener,
                    backwardRouterBindAddress
                ).freeze()
            },
            job = ::loopJob
        )
    }


    actual fun invokeSafe(block: (LoopState<S>) -> Unit) {
        isolatedQueue.access {
            it.queue.addLast(InvokeQuery(block))
            ZmqMsg().send(it.pingSocket)
        }
    }

    actual fun stopSafe() {
        isolatedQueue.access {
            it.queue.addLast(StopQuery())
            ZmqMsg().send(it.pingSocket)
        }
    }

}
