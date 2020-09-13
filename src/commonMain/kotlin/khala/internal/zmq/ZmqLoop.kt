package khala.internal.zmq

internal expect class ZmqLoop<S>(
    context: ZmqContext,
    userStateProducer: () -> S,
    forwardListener: LoopState<S>.(String, ZmqMsg) -> Unit,
    backwardListener: LoopState<S>.(ZmqMsg) -> Unit,
    backwardRouterBindAddress: String?
) {

    fun invokeSafe(
        block: LoopState<S>.() -> Unit
    )

    fun stopSafe()

}