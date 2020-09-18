package khala.internal.events.server

import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.server.ServerLoopScope

internal fun ServerLoopScope<ServerState>.backwardListener(
    serverState: ServerState,
    msg: ZmqMsg
) {

}