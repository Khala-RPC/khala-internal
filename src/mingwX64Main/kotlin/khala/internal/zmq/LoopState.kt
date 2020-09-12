package khala.internal.zmq

internal actual fun <S> LoopState<S>.sendForward(address: String, msg: ZmqMsg) {
    val socket = forwardSockets[address] ?: run {
        val newSocket = context.createAndConnectDealer(address)
        forwardSockets[address] = newSocket
        newSocket
    }
    msg.send(socket)
}

internal actual fun <S> LoopState<S>.remove(address: String) {
    forwardSockets.remove(address)?.close()
}