package khala.internal.zmq.bindings

external class Buffer {

    companion object {
        fun from(str: String, encoding: String = definedExternally): Buffer
    }

    fun toString(encoding: String = definedExternally, start: Int = definedExternally, end: Int = definedExternally): String

}

internal fun Buffer.bufferToString(): String = this.toString("utf8")

actual typealias ZmqBinaryData = Buffer

internal actual fun ZmqBinaryData.toByteArray(): ByteArray = TODO()

internal actual fun ByteArray.toZmqBinaryData(): ZmqBinaryData = TODO()