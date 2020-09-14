package khala.internal.json

import khala.internal.cinterop.json.json_object_get_int
import khala.internal.cinterop.json.json_object_get_type
import khala.internal.cinterop.json.json_object_get_userdata
import khala.internal.cinterop.json.json_tokener_parse
import kotlinx.cinterop.*
import kotlin.test.Test

class JsonTest {

    @Test
    fun testJson() {
        val jsonObj = json_tokener_parse("12345")
        val type = json_object_get_type(jsonObj)
        println(type)
        val data = json_object_get_int(jsonObj)
        println(data)
        /*
        println(json_tokener_parse("null"))
        println(json_tokener_parse("true")!!.pointed.reinterpret<BooleanVar>().value)
        //println(json_tokener_parse("12345")!!.pointed.reinterpret<IntVar>().value)
        //println(json_tokener_parse("12345")!!.reinterpret<IntVar>().pointed.value)
        println(json_tokener_parse("12345")!!.pointed)

        println(json_tokener_parse("12345.54321")!!.pointed.reinterpret<DoubleVar>().value)
        println(json_tokener_parse("\"privet\"")!!.pointed.reinterpret())
        println(json_tokener_parse("[1, 2, 3, 4, 5]")!!.pointed)
        println(json_tokener_parse("""{ "lol": "ku", "pri": 1234 }""")!!.pointed)*/
    }

}