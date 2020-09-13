package khala.internal.zmq

import co.touchlab.stately.isolate.IsolateState
import kotlinx.cinterop.pin
import kotlinx.cinterop.staticCFunction
import platform.posix.sleep
import kotlin.native.concurrent.AtomicInt
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze

private val loopCounter = AtomicInt(0)

internal class QueueState<S>(val queue: ArrayDeque<LoopQuery<S>>, val pingSocket: ZmqSocket)

internal fun <S> createIsolateState(ctx: ZmqContext, loopId: Int) = IsolateState {
    QueueState(
        ArrayDeque<LoopQuery<S>>(),
        ctx.createAndBindDealer(
            "inproc://LOOP_WORKER_$loopId"
        )

    )
}

internal actual class ZmqLoop<S> actual constructor(
    val context: ZmqContext,
    val userStateProducer: () -> S,
    val forwardListener: LoopState<S>.(String, ZmqMsg) -> Unit,
    val backwardListener: LoopState<S>.(ZmqMsg) -> Unit,
    val backwardRouterBindAddress: String?
)  {

    internal val loopId = loopCounter.addAndGet(1)

    private val loopWorker = Worker.start(name = "LOOP_WORKER_$loopId")

    internal val isolatedQueue = createIsolateState<S>(context, loopId)

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

    actual fun invokeSafe(block: LoopState<S>.() -> Unit) {
        isolatedQueue.access {
            it.queue.addLast(InvokeQuery(block))
            val m = ZmqMsg()
            m.addString("")
            m.send(it.pingSocket)
        }
    }

    actual fun stopSafe() {
        isolatedQueue.access {
            it.queue.addLast(StopQuery())
            ZmqMsg().send(it.pingSocket)
        }
    }

}
