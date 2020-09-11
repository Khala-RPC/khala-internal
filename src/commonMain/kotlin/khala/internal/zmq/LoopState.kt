package khala.internal.zmq

internal class LoopState<S>(
    val loop: ZmqLoop<S>,
    val context: ZmqContext,
    val forwardSockets: MutableMap<String, ZmqSocket>,
    val forwardListener: LoopState<S>.(String, ZmqMsg) -> Unit,
    val backwardSocket: ZmqSocket?,
    val backwardListener: LoopState<S>.(ZmqMsg) -> Unit,
    val userState: S,
    var isStopped: Boolean = false
)

internal fun <S> LoopState<S>.sendForward(address: String, msg: ZmqMsg) {
    val socket = forwardSockets[address] ?: run {
        val new = context.createAndConnectDealer(address)
        forwardSockets[address] = new
        new
    }
    msg.send(socket)
}

internal fun <S> LoopState<S>.remove(address: String) {
    forwardSockets.remove(address)?.close()
}
