package khala.internal.json

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

internal actual fun parseJson(jsonString: String): Any? {
    val parsed = JSONTokener(jsonString).nextValue()
    return parsedToStructured(parsed)
}

private fun parsedToStructured(parsed: Any?): Any? {
    parsed ?: return null
    return when (parsed) {
        JSONObject.NULL -> null
        is Boolean, is Int, is Double, is String -> parsed
        is JSONArray -> jsonArrayToStructured(parsed)
        is JSONObject -> jsonObjectToStructured(parsed)
        else -> error("Can't serialize $parsed to JSON")
    }
}

private fun jsonArrayToStructured(parsed: JSONArray): Any? {
    return parsed.map { parsedToStructured(it) }
}

private fun jsonObjectToStructured(parsed: JSONObject): Any? {
    val map = mutableMapOf<String, Any?>()
    parsed.keys().forEach {
        map[it] = parsedToStructured(parsed[it])
    }
    return map
}