package khala.internal.zmq

internal actual class ZmqLoop<S> actual constructor(
        context: ZmqContext,
        userStateProducer: () -> S,
        forwardListener: LoopState<S>.(String, ZmqMsg) -> Unit,
        backwardListener: LoopState<S>.(ZmqMsg) -> Unit,
        backwardRouterBindAddress: String?
) {

    internal val backwardSocket = backwardRouterBindAddress?.let {
        context.createAndBindRouter(it)
    }

    internal val loopState = LoopState(
            loop = this,
            context = context,
            forwardSockets = LinkedHashMap(),
            forwardListener = forwardListener,
            backwardSocket = backwardSocket,
            backwardListener = backwardListener,
            userState = userStateProducer(),
            isStopped = false
    )


    actual fun invokeSafe(block: (LoopState<S>) -> Unit) {
        block(loopState)
    }

    actual fun stopSafe() {
        with(loopState) {
            isStopped = true
            forwardSockets.values.forEach {
                it.close()
            }
            backwardSocket?.close()
        }
    }
}