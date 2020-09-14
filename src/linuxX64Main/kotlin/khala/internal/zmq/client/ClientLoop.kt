package khala.internal.zmq.client

import co.touchlab.stately.isolate.IsolateState
import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.ZmqSocket
import kotlin.native.concurrent.AtomicInt
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze


internal class ClientQueueState<L, S>(val queue: ArrayDeque<ClientLoopQuery<L, S>>, val pingSocket: ZmqSocket)

internal fun <L, S> createIsolateState(loopId: Int) = IsolateState {
    ClientQueueState(
        ArrayDeque<ClientLoopQuery<L, S>>(),
        ZmqContext.createAndBindDealer(
            "inproc://LOOP_WORKER_$loopId"
        )
    )
}

internal actual class ClientLoop<L, S> actual constructor(
    loopStateProducer: () -> L,
    socketStateProducer: (L) -> S,
    forwardListener: ForwardListener<L, S>
)  {

    private companion object {
        private val loopCounter = AtomicInt(0)
    }

    private val loopId = loopCounter.addAndGet(1)

    private val loopWorker = Worker.start(name = "LOOP_WORKER_$loopId")

    private val isolatedQueue: IsolateState<ClientQueueState<L, S>> = createIsolateState(loopId)

    init {
        isolatedQueue.access { } // wait until inproc socket binds
        loopWorker.execute(
            mode = TransferMode.SAFE,
            producer = {
                ClientLoopJobInitialState(
                    isolatedQueue, loopId,
                    loopStateProducer, socketStateProducer,
                    forwardListener
                ).freeze()
            },
            job = ::clientLoopJob
        )
    }

    actual fun invokeSafe(block: ClientLoopScope<L, S>.(L) -> Unit) {
        isolatedQueue.access {
            it.queue.addLast(ClientLoopQuery.InvokeQuery(block))
            val m = ZmqMsg()
            m.addString("")
            m.send(it.pingSocket)
        }
    }

    actual fun stopSafe() {
        isolatedQueue.access {
            it.queue.addLast(ClientLoopQuery.StopQuery())
            ZmqMsg().send(it.pingSocket)
        }
        isolatedQueue.dispose()
    }

}
