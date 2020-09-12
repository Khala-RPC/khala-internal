@file:JvmName("ZmqMsgActualKt")
package khala.internal.zmq

import kotlin.jvm.JvmName

expect class BinaryData

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

internal fun sendMsg(socket: ZmqSocket, block: MsgBuilder.() -> Unit) {
    val msg = ZmqMsg()
    val builder = MsgBuilder(msg)
    builder.block()
    msg.send(socket)
}