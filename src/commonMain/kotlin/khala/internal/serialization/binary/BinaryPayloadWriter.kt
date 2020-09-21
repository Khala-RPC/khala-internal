package khala.internal.serialization.binary

import khala.internal.events.functions.LocalFunction
import khala.internal.events.functions.NamedFunctionLink
import khala.internal.serialization.PayloadWriter
import kotlinx.io.ByteArrayOutput
import kotlinx.io.text.writeUtf8String
import kotlinx.io.writeDouble
import kotlinx.io.writeInt
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@ExperimentalJsExport
@JsExport
class BinaryPayloadWriter : PayloadWriter() {

    private val bytePacketBuilder = ByteArrayOutput()

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
        bytePacketBuilder.writeUtf8String(str)
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

    override fun buildBinary(): ByteArray = bytePacketBuilder.toByteArray()

}