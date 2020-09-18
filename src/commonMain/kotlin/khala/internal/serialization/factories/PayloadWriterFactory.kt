package khala.internal.serialization.factories

import khala.internal.serialization.*
import khala.internal.serialization.binary.BinaryPayloadWriter
import khala.internal.serialization.json.JsonPayloadWriter

internal fun getPayloadWriterFactory(serializationProtocol: SerializationProtocol): PayloadWriterFactory? =
    when (serializationProtocol) {
        BINARY -> BinaryPayloadWriterFactory()
        JSON -> JsonPayloadWriterFactory()
        else -> null
    }

internal interface PayloadWriterFactory {

    fun getPayloadWriter(): PayloadWriter

}

private class BinaryPayloadWriterFactory : PayloadWriterFactory {

    override fun getPayloadWriter(): PayloadWriter =
        BinaryPayloadWriter()

}

private class JsonPayloadWriterFactory : PayloadWriterFactory {

    override fun getPayloadWriter(): PayloadWriter =
        JsonPayloadWriter()

}