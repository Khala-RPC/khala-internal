package khala.internal.serialization.json

import khala.internal.cinterop.json.*
import kotlinx.cinterop.*
import kotlin.test.Test

class JsonTest {

    fun Any.withClass() = Pair(this, this::class)

    @Test
    fun testJsonParse() {
        var jsonObj = json_tokener_parse("12345")
        println("${json_object_get_type(jsonObj).name} ${json_object_get_int(jsonObj).withClass()}")
        jsonObj = json_tokener_parse("12345.54321")
        println("${json_object_get_type(jsonObj).name} ${json_object_get_double(jsonObj).withClass()}")
        jsonObj = json_tokener_parse("\"privet\"")
        println("${json_object_get_type(jsonObj).name} ${json_object_get_string(jsonObj)!!.toKString().withClass()}")
        jsonObj = json_tokener_parse("[1, 2, 3, 4, 5]")
        println("${json_object_get_type(jsonObj).name} ${json_object_get_array(jsonObj)!!.withClass()}")
        jsonObj = json_tokener_parse("""{ "lol": "ku", "pri": 1234 }""")
        println("${json_object_get_type(jsonObj).name} ${json_object_get_object(jsonObj)!!.withClass()}")
        jsonObj = json_tokener_parse("null")
        println("${json_object_get_type(jsonObj).name} ${json_object_get(jsonObj)?.withClass()}")
    }

    fun CPointer<array_list>.get(idx: Int): COpaquePointer? {
        val arr = pointed
        if (idx >= arr.length.convert<Int>()) return null
        return arr.array!![idx]
    }

    @Test
    fun testJsonArrayParse() {
        val jsonArray = json_object_get_array(json_tokener_parse("[1, 2, 3, 4, 5]"))!!
        val elem = jsonArray.get(4)
        println(jsonArray.pointed.length)
        println(jsonArray.pointed.size)
        println(elem)
        println(json_object_get_type(elem!!.reinterpret()))
    }

    @Test
    fun testJsonObjectParse() {
        val obj: CPointer<json_object>? = json_tokener_parse("""{ "lol": "ku", "pri": 1234 }""")
        val jsonObject = json_object_get_object(obj)!!
        val count = jsonObject.pointed.count
        val map = hashMapOf<Any?, Any?>()
        var cur = jsonObject.pointed.head
        repeat(count) {
            val wtf = cur!!.pointed
            println(wtf.k!!.reinterpret<ByteVar>().toKString())
            println(wtf.v!!.reinterpret<ByteVar>().toKString())
            map[wtf.k] = wtf.v
            cur = wtf.next
        }
        //json_object_get(jsonObject.pointed, "pri")
        //val elem = json_object_object_get(jsonObject.pointed, "pri")
    }

    @Test
    fun testJsonObjectIteration() {
        memScoped {
            val jsonObject = json_tokener_parse("""{ "lol": "ku", "pri": 1234 }""")
            val lhTable = json_object_get_object(jsonObject)
            println(lhTable!!.pointed.count)
            val iter: CPointer<json_object_iterator> = json_object_iter_begin(jsonObject).getPointer(this)
            val end = json_object_iter_end(jsonObject).getPointer(this)
            while (json_object_iter_equal(iter, end) == 0) {
                json_object_iter_peek_name(iter)!!.toKString().print()
                json_object_get_string(json_object_iter_peek_value(iter))!!.toKString().print()
                json_object_get_type(json_object_iter_peek_value(iter)).print()
                json_object_iter_next(iter)
            }
        }
    }

    fun Any?.print() = println(this)

}