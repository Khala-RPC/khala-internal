package khala.internal.serialization.factories

import khala.internal.serialization.BINARY
import khala.internal.serialization.JSON
import khala.internal.serialization.PayloadReader
import khala.internal.serialization.SerializationProtocol
import khala.internal.serialization.binary.BinaryPayloadReader
import khala.internal.serialization.json.JsonPayloadReader
import kotlin.js.ExperimentalJsExport

@ExperimentalJsExport
internal fun getPayloadReaderFactory(serializationProtocol: SerializationProtocol): PayloadReaderFactory? =
    when (serializationProtocol) {
        BINARY -> BinaryPayloadReaderFactory()
        JSON -> JsonPayloadReaderFactory()
        else -> null
    }

@ExperimentalJsExport
internal interface PayloadReaderFactory {

    fun getPayloadReader(bytes: ByteArray): PayloadReader

}

@ExperimentalJsExport
private class BinaryPayloadReaderFactory : PayloadReaderFactory {

    override fun getPayloadReader(bytes: ByteArray): PayloadReader =
        BinaryPayloadReader(bytes)

}

@ExperimentalJsExport
private class JsonPayloadReaderFactory : PayloadReaderFactory {

    override fun getPayloadReader(bytes: ByteArray): PayloadReader =
        JsonPayloadReader(bytes)

}