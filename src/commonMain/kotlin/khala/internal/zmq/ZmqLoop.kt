package khala.internal.zmq

/*
internal expect class ZmqLoop<S, L>(context: ZmqContext, loopStateProducer: () -> L) {

    fun add(
        socket: ZmqSocket,
        socketStateProducer: () -> S,
        onNewMessage: (ZmqMsg, S, L) -> Unit
    ): Int

    fun remove(socketID: Int)

    fun start()

    fun stop()

}*/