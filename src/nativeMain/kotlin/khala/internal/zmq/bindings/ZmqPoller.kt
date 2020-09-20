package khala.internal.zmq.bindings

internal expect fun poll(sockets: List<ZmqSocket>, onMsg: (idx: Int, msg: ZmqMsg) -> Unit)