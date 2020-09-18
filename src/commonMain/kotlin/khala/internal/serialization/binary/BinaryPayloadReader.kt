package khala.internal.serialization.binary

import khala.internal.events.functions.NamedFunctionLink
import khala.internal.events.functions.RemoteFunctionLink
import khala.internal.serialization.PayloadReader
import kotlinx.io.ByteArrayInput
import kotlinx.io.readByte
import kotlinx.io.readDouble
import kotlinx.io.readInt
import kotlinx.io.text.readUtf8String

class BinaryPayloadReader(bytes: ByteArray) : PayloadReader(bytes) {

    private val bytePacket = ByteArrayInput(bytes)

    fun readByte(): Byte = bytePacket.readByte()

    fun readBoolean(): Boolean = bytePacket.readByte() != 0.toByte()

    fun readInt(): Int = bytePacket.readInt()

    fun readDouble(): Double = bytePacket.readDouble()

    fun readString(): String {
        val length = bytePacket.readInt()
        return bytePacket.readUtf8String(length)
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