package khala.internal.zmq.server

import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.bindings.ZmqMsg

internal val varargWrapper = js("function(f) { return function() { return f(arguments); }; }")

internal actual class ServerLoop<L> actual constructor(
    loopStateProducer: () -> L,
    backwardRouterBindAddress: String,
    backwardListener: BackwardListener<L>
) {

    private val backwardSocket = ZmqContext.createAndBindRouter(backwardRouterBindAddress)

    private val loopScope = ServerLoopScope(
        backwardSocket = backwardSocket,
        loopState = loopStateProducer(),
        isStopped = false
    )

    init {
        backwardSocket.socket.on("message", varargWrapper { args ->
            loopScope.run { backwardListener(loopState, ZmqMsg(args)) }
        })
    }


    actual fun invokeSafe(block: ServerLoopScope<L>.(L) -> Unit) {
        loopScope.run { block(loopState) }
    }

    actual fun stopSafe() {
        with(loopScope) {
            isStopped = true
            backwardSocket.close()
        }
    }
}