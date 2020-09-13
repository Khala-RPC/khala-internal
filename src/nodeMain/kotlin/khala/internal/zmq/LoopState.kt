package khala.internal.zmq

internal val varargWrapper = js("function(f) { return function() { return f(arguments); }; }")

internal actual fun <S> LoopState<S>.sendForward(address: String, msg: ZmqMsg) {
    val socket = forwardSockets[address] ?: run {
        val newSocket = context.createAndConnectDealer(address)
        forwardSockets[address] = newSocket
        newSocket.socket.on("message", varargWrapper { args -> forwardListener(address, ZmqMsg(args)) })
        newSocket
    }
    msg.send(socket)
}

internal actual fun <S> LoopState<S>.remove(address: String) {
    forwardSockets.remove(address)?.close()
}