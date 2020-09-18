package khala.internal.serialization.json

import khala.internal.events.functions.LocalFunction
import khala.internal.events.functions.NamedFunctionLink
import khala.internal.serialization.PayloadWriter

class JsonPayloadWriter : PayloadWriter() {

    private var structuredPayload: Structured = null

    fun setStructuredPayload(structured: Structured) {
        structuredPayload = structured
    }

    fun structureLocalFunction(localFunction: LocalFunction): Structured {
        val localFunctionID = putLocalFunction(localFunction)
        return mapOf("type" to "ANONYMOUS", "id" to localFunctionID)
    }

    fun structureNamedFunction(address: String, name: String): Structured {
        return mapOf("type" to "NAMED", "address" to address, "name" to name)
    }

    fun structureNamedFunctionLink(namedFunctionLink: NamedFunctionLink): Structured {
        return structureNamedFunction(namedFunctionLink.address, namedFunctionLink.name)
    }

    override fun buildBinary(): ByteArray {
        return writeJson(structuredPayload).encodeToByteArray()
    }

}