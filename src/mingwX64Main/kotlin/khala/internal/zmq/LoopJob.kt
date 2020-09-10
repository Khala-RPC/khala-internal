package khala.internal.zmq

internal class JobInitialState<S>(
    val loop: ZmqLoop<S>,
    val context: ZmqContext,
    val userStateProducer: () -> S,
    val forwardListener: LoopState<S>.(String, ZmqMsg) -> Unit,
    val backwardListener: LoopState<S>.(ZmqMsg) -> Unit,
    val backwardRouterBindAddress: String?
)

internal fun <S> loopJob(initialState: JobInitialState<S>) {

}