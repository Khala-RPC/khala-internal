package khala.internal.zmq.client


internal actual class ClientLoop<L, S> actual constructor(
    loopStateProducer: () -> L,
    socketStateProducer: (L) -> S,
    forwardListener: ForwardListener<L, S>
) {

    private val loopScope = ClientLoopScope(
        forwardSockets = LinkedHashMap(),
        forwardListener = forwardListener,
        socketStateProducer = socketStateProducer,
        loopState = loopStateProducer(),
        isStopped = false
    )

    actual fun invokeSafe(block: ClientLoopScope<L, S>.(L) -> Unit) {
        loopScope.run { block(loopState) }
    }

    actual fun stopSafe() {
        with(loopScope) {
            isStopped = true
            forwardSockets.values.forEach {
                it.socket.close()
            }
        }
    }
}