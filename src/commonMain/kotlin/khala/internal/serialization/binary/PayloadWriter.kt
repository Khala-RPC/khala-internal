package khala.internal.serialization.binary

import io.ktor.utils.io.core.*

class PayloadWriter {

    private val bytePacketBuilder = BytePacketBuilder()

    private val functionArgs = ArrayList<Unit>()

    fun writeByte(v: Byte) {
        bytePacketBuilder.writeByte(v)
    }

    fun writeInt(v: Int) {
        bytePacketBuilder.writeInt(v)
    }

    fun writeDouble(v: Double) {
        bytePacketBuilder.writeDouble(v)
    }

    fun writeString(str: String) {
        bytePacketBuilder.writeInt(str.length)
        bytePacketBuilder.append(str)
    }

    fun addLocalFunction() {

    }

    fun build(): ByteArray = bytePacketBuilder.build().readBytes()

}