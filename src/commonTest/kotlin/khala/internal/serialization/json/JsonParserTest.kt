package khala.internal.serialization.json

import khala.internal.serialization.json.parseJson
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonParserTest {

    @Test
    fun testBasicJsonParsing() {
        assertEquals(null, parseJson("null"))
        assertEquals(true, parseJson("true"))
        assertEquals(12345, parseJson("12345"))
        assertEquals(12345.54321, parseJson("12345.54321"))
        assertEquals("privet", parseJson("\"privet\""))
        assertEquals(listOf(null, false, 12, 2112, "ku"), parseJson("""[ null, false, 12, 2112, "ku" ]"""))
        assertEquals(
            mapOf("odin" to 1, "dva" to 22, "tri" to "rofl", "chetire" to mapOf<String, Any?>()),
            parseJson("""{
                |"odin": 1,
                |"dva": 22,
                |"tri": "rofl",
                |"chetire": { }
                |}""".trimMargin())
        )
    }

}