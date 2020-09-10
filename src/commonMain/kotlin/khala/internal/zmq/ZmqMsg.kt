package khala.internal.zmq

expect class BinaryData

internal expect class ZmqMsg() {

    fun send(socket: ZmqSocket)

    fun addBytes(bytes: BinaryData)
    fun addString(str: String)

    fun popBytes(): BinaryData
    fun popString(): String

    fun close()

}