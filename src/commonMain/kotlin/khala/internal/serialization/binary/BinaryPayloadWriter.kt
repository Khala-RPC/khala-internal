package khala.internal.serialization.binary

import io.ktor.utils.io.core.*
import khala.internal.events.client.LocalFunction
import khala.internal.events.client.RemoteFunction

class BinaryPayloadWriter {

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

    }

    fun addRemoteFunction(address: String, name: String) {

    }

    fun addRemoteFunctionLink(remoteFunctionLink: RemoteFunction) {
        addRemoteFunction(remoteFunctionLink.address, remoteFunctionLink.name)
    }


    fun build(): ByteArray = bytePacketBuilder.build().readBytes()

}