package khala.internal.json

import kotlin.test.Test
import kotlin.test.assertTrue

class JsonTest {

    inline fun <reified T> assertParsedClass(text: String) {
        assertTrue(JSON.parse<dynamic>(text) is T)
    }

    @Test
    fun testNodeJson() {
        assertParsedClass<Unit?>("null")
        assertParsedClass<Boolean>("true")
        assertParsedClass<Int>("12345")
        assertParsedClass<Double>("12345.54321")
        assertParsedClass<String>("\"privet\"")
        assertParsedClass<Array<*>>("[1, 2, 3, 4, 5]")
        val parsedObj = JSON.parse<dynamic>("""{ "lol": "ku", "pri": 1234 }""")
        console.log(parsedObj)
        println(parsedObj)
        println(jsTypeOf(parsedObj))
        println(parsedObj::class)
        assertParsedClass<Any>("""{ "lol": "ku", "pri": 1234 }""")
    }
}