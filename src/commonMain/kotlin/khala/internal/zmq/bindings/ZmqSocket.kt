package khala.internal.zmq.bindings

internal expect class ZmqSocket {

    fun close()

}

internal inline fun ZmqSocket.send(block: MsgBuilder.() -> Unit) {
    buildMsg(block).send(this)
}