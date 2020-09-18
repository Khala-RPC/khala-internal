package khala.internal.serialization.binary

import khala.internal.events.functions.LocalFunction
import khala.internal.events.functions.NamedFunctionLink
import khala.internal.serialization.PayloadWriter
import kotlinx.io.core.BytePacketBuilder
import kotlinx.io.core.readBytes

class BinaryPayloadWriter : PayloadWriter() {

    private val bytePacketBuilder = BytePacketBuilder()

    fun writeByte(v: Byte) {
        bytePacketBuilder.writeByte(v)
    }

    fun writeBoolean(v: Boolean) {
        bytePacketBuilder.writeByte(if (v) 1.toByte() else 0.toByte())
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

    fun addLocalFunction(localFunction: LocalFunction) {
        val localFunctionID = putLocalFunction(localFunction)
        bytePacketBuilder.writeByte(1)
        bytePacketBuilder.writeInt(localFunctionID)
    }

    fun addNamedFunction(address: String, name: String) {
        bytePacketBuilder.writeByte(0)
        writeString(address)
        writeString(name)
    }

    fun addNamedFunctionLink(namedFunctionLink: NamedFunctionLink) {
        addNamedFunction(namedFunctionLink.address, namedFunctionLink.name)
    }

    override fun buildBinary(): ByteArray = bytePacketBuilder.build().readBytes()

}