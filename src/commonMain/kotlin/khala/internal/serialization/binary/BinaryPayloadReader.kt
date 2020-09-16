package khala.internal.serialization.binary

import io.ktor.utils.io.core.*

class BinaryPayloadReader(bytes: ByteArray) {

    private val bytePacket: ByteReadPacket = ByteReadPacket(bytes)

    fun readByte(): Byte = bytePacket.readByte()

    fun readBoolean(): Boolean = bytePacket.readByte() != 0.toByte()

    fun readInt(): Int = bytePacket.readInt()

    fun readDouble(): Double = bytePacket.readDouble()

    fun readString(): String {
        val length = bytePacket.readInt()
        return bytePacket.readTextExact(length)
    }



}