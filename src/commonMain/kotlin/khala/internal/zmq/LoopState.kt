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

internal expect fun <S> LoopState<S>.sendForward(address: String, msg: ZmqMsg)
internal expect fun <S> LoopState<S>.remove(address: String)

internal fun <S> LoopState<S>.sendForward(address: String, block: MsgBuilder.() -> Unit) {
    val msg = ZmqMsg()
    val builder = MsgBuilder(msg)
    builder.block()
    sendForward(address, msg)
}