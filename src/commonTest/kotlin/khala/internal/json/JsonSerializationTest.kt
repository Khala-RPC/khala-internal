package khala.internal.json

import khala.internal.serialization.json.parseJson
import khala.internal.serialization.json.writeJson
import kotlin.test.Test
import kotlin.test.assertEquals

fun assertEquallyParse(expected: String, actual: String) {
    assertEquals(parseJson(expected), parseJson(actual))
}

class JsonSerializationTest {

    @Test
    fun testJsonSerialization() {
        val bigObject = mapOf(
            "odin" to listOf(1, "1234", true),
            "dva" to null,
            "tri" to false,
            "chetire" to mapOf(
                "chetire.odin" to 4321,
                "chetire.dva" to listOf<Any?>()
            ),
            "pyat" to listOf(mapOf("ku" to "poka"))
        )
        assertEquals(bigObject, parseJson(writeJson(bigObject)))
    }

}