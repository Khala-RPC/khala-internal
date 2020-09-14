package khala.internal.zmq.bindings

/**
 * Binary block that is received and sent inside ZMQ messages.
 * //TODO We need methods to convert this to ByteArray and back.
 * This is NOT ByteArray because ByteArray is not really interoperable with some platform-specific code.
 */
expect class BinaryData

/**
 * Binding for ZMQ Multipart Message
 */
internal expect class ZmqMsg() {

    fun send(socket: ZmqSocket)

    fun addBytes(bytes: BinaryData)
    fun addString(str: String)

    fun popBytes(): BinaryData
    fun popString(): String

    fun close()

}

internal class MsgBuilder(val msg: ZmqMsg) {

    operator fun BinaryData.unaryPlus() {
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