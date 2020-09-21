package khala.internal.serialization.json

import khala.internal.events.functions.NamedFunctionLink
import khala.internal.events.functions.RemoteFunctionLink
import khala.internal.serialization.PayloadReader
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@ExperimentalJsExport
@JsExport
class JsonPayloadReader(bytes: ByteArray) : PayloadReader(bytes) {

    fun readStructured(): Structured {
        return parseJson(bytes.decodeToString())
    }

    private fun throwInvalidLink(remoteFunctionLink: Structured): Nothing {
        error("Invalid payload: $remoteFunctionLink is not a function link")
    }

    fun readRemoteFunctionLink(remoteFunctionLink: Structured): RemoteFunctionLink {
        if (remoteFunctionLink !is Map<*, *>) throwInvalidLink(remoteFunctionLink)
        val linkType = remoteFunctionLink["type"]
        return when (linkType) {
            "NAMED" -> { // Named function
                val address = remoteFunctionLink["address"] as? String ?: throwInvalidLink(remoteFunctionLink)
                val name = remoteFunctionLink["name"] as? String ?: throwInvalidLink(remoteFunctionLink)
                NamedFunctionLink(address, name)
            }
            "ANONYMOUS" -> { // Anonymous function
                val theirFunctionID = remoteFunctionLink["id"] as? Int ?: throwInvalidLink(remoteFunctionLink)
                getTheirFunction(theirFunctionID)
            }
            else -> error("Invalid payload: function link type $linkType is not supported.")
        }
    }

}