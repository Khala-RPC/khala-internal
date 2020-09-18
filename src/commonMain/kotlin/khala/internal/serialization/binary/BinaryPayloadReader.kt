package khala.internal.serialization.binary

import khala.internal.events.functions.NamedFunctionLink
import khala.internal.events.functions.RemoteFunctionLink
import khala.internal.serialization.PayloadReader
import kotlinx.io.core.ByteReadPacket

class BinaryPayloadReader(bytes: ByteArray) : PayloadReader(bytes) {

    private val bytePacket: ByteReadPacket = ByteReadPacket(bytes)

    fun readByte(): Byte = bytePacket.readByte()

    fun readBoolean(): Boolean = bytePacket.readByte() != 0.toByte()

    fun readInt(): Int = bytePacket.readInt()

    fun readDouble(): Double = bytePacket.readDouble()

    fun readString(): String {
        val length = bytePacket.readInt()
        return bytePacket.readTextExact(length)
    }

    fun readRemoteFunctionLink(): RemoteFunctionLink {
        val linkType = bytePacket.readByte()
        return when (linkType) {
            0.toByte() -> { // Named function
                val address = readString()
                val name = readString()
                NamedFunctionLink(address, name)
            }
            1.toByte() -> { // Anonymous function
                val theirFunctionID = bytePacket.readInt()
                getTheirFunction(theirFunctionID)
            }
            else -> error("Invalid payload: function link type $linkType is not supported.")
        }
    }

}