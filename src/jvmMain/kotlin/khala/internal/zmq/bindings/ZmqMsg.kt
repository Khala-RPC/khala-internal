@file:JvmName("ZmqMsgActualKt")
package khala.internal.zmq.bindings

import org.zeromq.ZMsg

internal actual class ZmqMsg {

    companion object {

        fun recv(source: ZmqSocket): ZmqMsg? =
            ZmqMsg(ZMsg.recvMsg(source.socket))

    }

    private val message: ZMsg

    actual constructor() {
        message = ZMsg()
    }

    internal constructor(message: ZMsg) {
        this.message = message
    }

    actual fun send(socket: ZmqSocket) {
        message.send(socket.socket)
    }

    actual fun addBytes(bytes: ZmqBinaryData) {
        message.add(bytes)
    }

    actual fun addString(str: String) {
        message.add(str)
    }

    actual fun popBytes(): ZmqBinaryData {
        return message.pop().data
    }

    actual fun popString(): String {
        return message.popString()
    }

    actual fun close() {
        message.destroy()
    }
}