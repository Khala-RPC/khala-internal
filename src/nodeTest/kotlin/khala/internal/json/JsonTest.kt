package khala.internal.json

import kotlin.test.Test
import kotlin.test.assertTrue

class JsonTest {

    inline fun <reified T> assertParsedClass(text: String) {
        assertTrue(JSON.parse<dynamic>(text) is T)
    }

    @Test
    fun testWtf() {
        val wtf = JSON.parse<dynamic>("12345.123")
        println(jsTypeOf(wtf))
        println(wtf is Int)
        println(wtf)
    }

    @Test
    fun testWTFF() {
        val wtf = JSON.parse<dynamic>("{ \"asd\": 1, \"dsa\": 2 }")
        println(jsTypeOf(wtf))
        println(wtf is Array<*>)
        println(wtf)
        val wtff = JSON.parse<dynamic>("[ 1, 2, 3, 4 ]")
        println(jsTypeOf(wtff))
        println(wtff is Array<*>)
        println(wtff)
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
        val list = arrayListOf<dynamic>()
        assertParsedClass<Any>("""{ "lol": "ku", "pri": 1234 }""")
        js("""for (var k in {lol: '1234dsa'}) { console.log(k); }""")
        //js("""for (var k in parsedObj) { list.add(k); }""")
        val foo = js("""function(listAdd, obj) { for (var k in parsedObj) { listAdd(k + parsedObj[k]); } }""")
        foo({ x -> list.add(x) }, parsedObj)
        println(list)
    }
}