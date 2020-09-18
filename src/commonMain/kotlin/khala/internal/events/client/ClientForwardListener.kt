package khala.internal.events.client

import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.ZmqSocket
import khala.internal.zmq.client.ClientLoopScope

internal fun ClientLoopScope<ClientState, ClientSocketState>.forwardListener(
    clientState: ClientState,
    socketState: ClientSocketState,
    address: String,
    forwardSocket: ZmqSocket,
    msg: ZmqMsg
) {

}