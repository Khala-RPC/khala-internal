package khala.internal.zmq.bindings

/**
 * Binding for ZMQ Multipart Message
 */
internal expect class ZmqMsg() {

    fun send(socket: ZmqSocket)

    fun addBytes(bytes: ZmqBinaryData)
    fun addString(str: String)

    fun popBytes(): ZmqBinaryData
    fun popString(): String

    fun close()

}

internal class MsgBuilder(val msg: ZmqMsg) {

    operator fun ZmqBinaryData.unaryPlus() {
        msg.addBytes(this)
    }

    operator fun String.unaryPlus() {
        msg.addString(this)
    }

}

internal inline fun buildMsg(block: MsgBuilder.() -> Unit): ZmqMsg {
    val msg = ZmqMsg()
    val builder = MsgBuilder(msg)
    builder.block()
    return msg
}

internal inline fun sendMsg(socket: ZmqSocket, block: MsgBuilder.() -> Unit) {
    val msg = ZmqMsg()
    val builder = MsgBuilder(msg)
    builder.block()
    msg.send(socket)
}