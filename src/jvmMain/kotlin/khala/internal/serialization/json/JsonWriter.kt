package khala.internal.serialization.json

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONStringer

actual fun writeJson(structuredObject: Structured): String {
    return JSONStringer.valueToString(structuredToJson(structuredObject))
}

private fun structuredToJson(structured: Structured): Any? {
    structured ?: return JSONObject.NULL
    return when (structured) {
        is Boolean, is Int, is Double, is String -> structured
        is List<*> -> structuredToArray(structured)
        is Map<*, *> -> structuredToObject(structured)
        else -> error("Can't serialize $structured to JSON")
    }
}

private fun structuredToArray(structured: List<*>): Any? {
    return JSONArray(structured.map { structuredToJson(it) })
}

private fun structuredToObject(structured: Map<*, *>): Any? {
    return JSONObject(structured.mapValues { (_, v) -> structuredToJson(v) })
}