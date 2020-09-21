package khala.internal.serialization.factories

import khala.internal.serialization.*
import khala.internal.serialization.binary.BinaryPayloadWriter
import khala.internal.serialization.json.JsonPayloadWriter
import kotlin.js.ExperimentalJsExport

@ExperimentalJsExport
internal fun getPayloadWriterFactory(serializationProtocol: SerializationProtocol): PayloadWriterFactory? =
    when (serializationProtocol) {
        BINARY -> BinaryPayloadWriterFactory()
        JSON -> JsonPayloadWriterFactory()
        else -> null
    }

@ExperimentalJsExport
internal interface PayloadWriterFactory {

    fun getPayloadWriter(): PayloadWriter

}

@ExperimentalJsExport
private class BinaryPayloadWriterFactory : PayloadWriterFactory {

    override fun getPayloadWriter(): PayloadWriter =
        BinaryPayloadWriter()

}

@ExperimentalJsExport
private class JsonPayloadWriterFactory : PayloadWriterFactory {

    override fun getPayloadWriter(): PayloadWriter =
        JsonPayloadWriter()

}