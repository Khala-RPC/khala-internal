package khala.internal.serialization.json

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

actual fun parseJson(jsonString: String): Structured {
    val parsed = JSONTokener(jsonString).nextValue()
    return parsedToStructured(parsed)
}

private fun parsedToStructured(parsed: Any?): Structured {
    parsed ?: return null
    return when (parsed) {
        JSONObject.NULL -> null
        is Boolean, is Int, is Double, is String -> parsed
        is JSONArray -> jsonArrayToStructured(parsed)
        is JSONObject -> jsonObjectToStructured(parsed)
        else -> error("Can't serialize $parsed to JSON")
    }
}

private fun jsonArrayToStructured(parsed: JSONArray): Structured {
    return parsed.map { parsedToStructured(it) }
}

private fun jsonObjectToStructured(parsed: JSONObject): Structured {
    val map = mutableMapOf<String, Any?>()
    parsed.keys().forEach {
        map[it] = parsedToStructured(parsed[it])
    }
    return map
}