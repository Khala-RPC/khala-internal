package khala.internal.serialization.json

import cnames.structs.json_object
import khala.internal.cinterop.json.*
import kotlinx.cinterop.*

actual fun parseJson(jsonString: String): Structured {
    return parseJson(json_tokener_parse(jsonString))
}

private fun parseJson(jsonObject: CPointer<json_object>?): Structured {
    jsonObject ?: return null
    return when (json_object_get_type(jsonObject)) {
        json_type.json_type_null -> null
        json_type.json_type_boolean -> (json_object_get_boolean(jsonObject) != 0)
        json_type.json_type_int -> json_object_get_int(jsonObject)
        json_type.json_type_double -> json_object_get_double(jsonObject)
        json_type.json_type_string -> json_object_get_string(jsonObject)?.toKString()
        json_type.json_type_array -> jsonObject.toList()
        json_type.json_type_object -> jsonObject.toMap()
    }
}

private fun CPointer<json_object>.toList(): List<Any?>? {
    val jsonArray = json_object_get_array(this)?.pointed ?: return null
    val length = jsonArray.length.convert<Int>()
    return List(length) { parseJson(jsonArray.array?.get(it)?.reinterpret()) }
}

private fun CPointer<json_object>.toMap(): Map<String, Any?> {
    val map = mutableMapOf<String, Any?>()
    memScoped {
        val iter = json_object_iter_begin(this@toMap).getPointer(this)
        val endIter = json_object_iter_end(this@toMap)
        while (json_object_iter_equal(iter, endIter) == 0) {
            val key = json_object_iter_peek_name(iter)?.toKString()
            val value = json_object_iter_peek_value(iter)
            if (key != null) map[key] = parseJson(value)
            json_object_iter_next(iter)
        }
    }
    return map
}