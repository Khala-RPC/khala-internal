package khala.internal.json

internal actual fun writeJson(jsonObject: Any?): String {
    return JSON.stringify(objectToStructured(jsonObject))
}

private fun objectToStructured(obj: Any?): Any? {
    if (obj is List<*>) {
        return Array(obj.size) { objectToStructured(obj[it]) }
    }
    if (obj is Map<*, *>) {
        val structured = js("{}")
        obj.forEach { (k, v) -> structured[k.toString()] = objectToStructured(v) }
        return structured
    }
    return obj
}