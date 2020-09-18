package khala.internal.serialization.json

import khala.internal.cinterop.json.*
import kotlinx.cinterop.*

actual fun writeJson(structuredObject: Structured): String {
    val json = jsonObject.toJson()
    return json_object_to_json_string(json)?.toKString() ?: error("Error while serializing $jsonObject to JSON")
}

private fun Structured.toJson(): CPointer<json_object>? {
    this ?: return null
    return when (this) {
        is Boolean -> json_object_new_boolean(if (this) 1 else 0)
        is Int -> json_object_new_int(this)
        is Double -> json_object_new_double(this)
        is String -> json_object_new_string(this)
        is List<*> -> this.toJsonList()
        is Map<*, *> -> this.toJsonObject()
        else -> error("$this can't be serialized to JSON")
    }
}

private fun List<*>.toJsonList(): CPointer<json_object>? {
    /* CINTEROP ON LINUX DOES NOT SEE json_object_new_array_ext FUNCTION, SO ANOTHER FUNCTION IS USED HERE */
    /* DO NOT COPY-PASTE CODE FROM MINGW SOURCES BLINDLY */
    val arr = json_object_new_array()
    repeat(this.size) {
        json_object_array_put_idx(arr, it.convert(), this[it].toJson())
    }
    return arr
}

private fun Map<*, *>.toJsonObject(): CPointer<json_object>? {
    val objPointer = json_object_new_object()
    forEach { (key, value) ->
        json_object_object_add(objPointer, key.toString(), value.toJson())
    }
    return objPointer
}