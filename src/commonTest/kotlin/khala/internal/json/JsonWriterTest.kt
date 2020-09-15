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
        assertEquals(
            """[ null, false, 12, 2112, "ku" ]""".filterNot { it.isWhitespace() },
            writeJson(listOf(null, false, 12, 2112, "ku")).filterNot { it.isWhitespace() }
        )
        assertEquals(
            """{ "odin": 1, "dva": 22, "tri": "rofl", "chetire": { } }""".filterNot { it.isWhitespace() },
                writeJson(mapOf("odin" to 1, "dva" to 22, "tri" to "rofl", "chetire" to mapOf<String, Any?>()))
                    .filterNot { it.isWhitespace() }
        )
    }

}