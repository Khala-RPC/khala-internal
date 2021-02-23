package khala.internal.zmq.server

import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.ZmqSocket

private val setTimeout = js("function(f, d) { setTimeout(f, d); }")

internal actual class ServerLoopScope<L>(
    val backwardSocket: ZmqSocket,
    val loopState: L,
    var isStopped: Boolean
) {

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
        setTimeout({ block(this, loopState) }, timeoutMillis)
    }

}