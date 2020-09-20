package khala.internal.zmq.client

import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.rawSocket

private val varargWrapper = js("function(f) { return function() { return f(arguments); }; }")
private val setTimeout = js("function(f, d) { setTimeout(f, d); }")

internal actual class ClientLoopScope<L, S>(
    val forwardSockets: MutableMap<String, SocketWithState<S>>,
    val forwardListener: ForwardListener<L, S>,
    val socketStateProducer: (L) -> S,
    val loopState: L,
    var isStopped: Boolean
) {

    actual fun sendMessage(address: String, msg: ZmqMsg) {
        val socket = forwardSockets[address]?.socket ?: run {
            val newSocket = ZmqContext.createAndConnectDealer(address)
            val newSocketState = socketStateProducer(loopState)
            forwardSockets[address] = SocketWithState(newSocket, newSocketState)
            newSocket.rawSocket.on("message", varargWrapper { args -> forwardListener(
                this,
                loopState,
                newSocketState,
                address,
                newSocket,
                ZmqMsg(args)
            ) })
            newSocket
        }
        msg.send(socket)
    }

    actual fun remove(address: String) {
        forwardSockets.remove(address)?.socket?.close()
    }


    actual fun invokeAfterTimeout(
        timeoutMillis: Long,
        block: ClientLoopScope<L, S>.(L) -> Unit
    ) {
        setTimeout({ block(this, loopState) }, timeoutMillis)
    }

}