package khala.internal.zmq.bindings

external class Buffer {

    companion object {
        fun from(str: String, encoding: String = definedExternally): Buffer
    }

    fun toString(encoding: String = definedExternally, start: Int = definedExternally, end: Int = definedExternally): String

}

fun Buffer.bufferToString(): String = this.toString("utf8")

actual typealias BinaryData = Buffer

internal actual class ZmqMsg actual constructor() {

    private val frames: ArrayDeque<Buffer> = ArrayDeque()

    // From arguments object
    constructor(messages: dynamic) : this() {
        val length = messages.length
        for (i in 0 until length) {
            frames.addLast(messages[i])
        }
    }


    actual fun send(socket: ZmqSocket) {
        val byteBlocks = frames.toTypedArray()
        socket.rawSocket.send(byteBlocks)
        close()
    }

    actual fun addBytes(bytes: BinaryData) {
        frames.addLast(bytes)
    }

    actual fun addString(str: String) {
        frames.addLast(Buffer.from(str))
    }

    actual fun popBytes(): BinaryData = frames.removeFirst() as Buffer

    actual fun popString(): String = frames.removeFirst().toString("utf8")//(Int8Array(frames.removeFirst().buffer as ArrayBuffer) as ByteArray).toString()

    actual fun close() {
        frames.clear()
    }

}