package khala.internal.serialization.json

actual fun writeJson(structuredObject: Structured): String {
    return JSON.stringify(structuredToJsonObject(structuredObject))
}

private fun structuredToJsonObject(obj: Structured): Any? {
    if (obj is List<*>) {
        return Array(obj.size) { structuredToJsonObject(obj[it]) }
    }
    if (obj is Map<*, *>) {
        val structured = js("{}")
        obj.forEach { (k, v) -> structured[k.toString()] = structuredToJsonObject(v) }
        return structured
    }
    return obj
}