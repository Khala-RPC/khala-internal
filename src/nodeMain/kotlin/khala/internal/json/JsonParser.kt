package khala.internal.json

internal actual fun parseJson(jsonString: String): Any? {
    val parsedObject = JSON.parse<Any?>(jsonString)
    return parsedToStructured(parsedObject)
}

private fun parsedToStructured(parsed: Any?): Any? {
    parsed ?: return null
    return when (parsed) {
        // DO NOT JOIN THIS CHECKS IN ONE LINE, IT WILL NOT WORK BECAUSE OF STUPID JS TYPE SYSTEM
        is Boolean -> parsed
        is Int -> parsed
        is Double -> parsed
        is String -> parsed
        is Array<*> -> parsedArrayToStructured(parsed)
        else -> parsedObjectToStructured(parsed)
    }
}

private fun parsedArrayToStructured(parsed: Array<*>): Any? {
    return parsed.map { parsedToStructured(it) }
}

private val objectPropertiesGetter =
    js("""function(obj, addEntry) { for (var k in obj) { addEntry(k, obj[k]); } }""")

private fun parsedObjectToStructured(parsed: dynamic): Any? {
    parsed ?: return null
    val map = mutableMapOf<String, Any?>()
    objectPropertiesGetter(parsed) { key, value ->
        map[key.toString()] = parsedToStructured(value)
        Unit
    }
    return map
}