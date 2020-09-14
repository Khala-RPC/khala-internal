package khala.internal.json

import org.json.JSONTokener
import kotlin.test.Test


class JsonTest {

    fun getJson(str: String): Any = JSONTokener(str).nextValue()

    @Test
    fun testJson() {
        println(getJson("null")::class)
        println(getJson("true")::class)
        println(getJson("12345")::class)
        println(getJson("12345.54321")::class)
        println(getJson("\"privet\"")::class)
        println(getJson("[1, 2, 3, 4, 5]")::class)
        println(getJson("""{ "lol": "ku", "pri": 1234 }""")::class)
    }

}