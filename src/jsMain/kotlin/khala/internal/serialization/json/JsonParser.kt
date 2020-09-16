package khala.internal.serialization.json

actual fun parseJson(jsonString: String): Structured {
    val parsedObject = JSON.parse<Any?>(jsonString)
    return parsedToStructured(parsedObject)
}

private fun parsedToStructured(parsed: Any?): Structured {
    parsed ?: return null
    return when (parsed) {
        is Boolean, is Int, is Double, is String -> parsed
        is Array<*> -> parsedArrayToStructured(parsed)
        else -> parsedObjectToStructured(parsed)
    }
}

private fun parsedArrayToStructured(parsed: Array<*>): Structured {
    return parsed.map { parsedToStructured(it) }
}

private val objectPropertiesGetter =
    js("""function(obj, addEntry) { for (var k in obj) { addEntry(k, obj[k]); } }""")

private fun parsedObjectToStructured(parsed: dynamic): Structured {
    parsed ?: return null
    val map = mutableMapOf<String, Any?>()
    objectPropertiesGetter(parsed) { key, value ->
        map[key.toString()] = parsedToStructured(value)
        Unit
    }
    return map
}