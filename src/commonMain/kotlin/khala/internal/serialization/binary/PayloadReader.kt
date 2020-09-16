package khala.internal.serialization.binary

import io.ktor.utils.io.core.*

class PayloadReader(bytes: ByteArray) {

    private val bytePacket: ByteReadPacket = ByteReadPacket(bytes)

    fun readByte(): Byte = bytePacket.readByte()

    fun readInt(): Int = bytePacket.readInt()

    fun readDouble(): Double = bytePacket.readDouble()

    fun readString(): String {
        val length = bytePacket.readInt()
        return bytePacket.readTextExact(length)
    }

}