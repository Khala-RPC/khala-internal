package khala.internal.json

import kotlin.test.Test
import kotlin.test.assertEquals

class JsonWriterTest {

    @Test
    fun testBasicJsonWriting() {
        assertEquals("null", writeJson(null))
        assertEquals("true", writeJson(true))
        assertEquals("12345", writeJson(12345))
        assertEquals("12345.54321", writeJson(12345.54321))
        assertEquals("\"privet\"", writeJson("privet"))
        assertEquallyParse(
            """[ null, false, 12, 2112, "ku" ]""",
            writeJson(listOf(null, false, 12, 2112, "ku"))
        )
        assertEquallyParse(
            """{ "odin": 1, "dva": 22, "tri": "rofl", "chetire": { } }""",
                writeJson(mapOf("odin" to 1, "dva" to 22, "tri" to "rofl", "chetire" to mapOf<String, Any?>()))

        )
    }

}