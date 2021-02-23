package khala.internal.zmq.bindings



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

    actual fun addBytes(bytes: ZmqBinaryData) {
        frames.addLast(bytes)
    }

    actual fun addString(str: String) {
        frames.addLast(Buffer.from(str))
    }

    actual fun popBytes(): ZmqBinaryData = frames.removeFirst() as Buffer

    actual fun popString(): String = frames.removeFirst().toString("utf8")//(Int8Array(frames.removeFirst().buffer as ArrayBuffer) as ByteArray).toString()

    actual fun close() {
        frames.clear()
    }

}