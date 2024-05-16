package space.iseki.bencoding

import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlin.test.Test
import kotlin.test.assertEquals

class MapLikeTest {
    val data = "d3:abci10e2:cdi-20ee"
    val value = mapOf("abc" to 10, "cd" to -20)

    @Test
    fun testDecode() {
        val decoded = Bencode.decodeFromByteArray<Map<String, Int>>(data.encodeToByteArray())
        assertEquals(value, decoded)
    }

    @Test
    fun testManyDecode() {
        val data = (0..100).joinToString("", prefix = "d", postfix = "e") { val k = "$it"; "${k.length}:${k}i${k}e" }
        val value = (0..100).associateBy { val k = "$it"; k }
        val decoded = Bencode.decodeFromByteArray<Map<String, Int>>(data.encodeToByteArray())
        assertEquals(value, decoded)
    }

    @Test
    fun testEncode() {
        val encoded = Bencode.encodeToByteArray(value)
        assertEquals(data, encoded.decodeToString())
    }

    @Test
    fun testManyEncode() {
        val data = (0..11).associateBy { val k = "$it"; k }
        val value = (0..11).map { "$it" }.sorted() // ordered
            .joinToString("", prefix = "d", postfix = "e") { k -> "${k.length}:${k}i${k}e" }
        val encoded = Bencode.encodeToByteArray(data)
        assertEquals(value, encoded.decodeToString())
    }
}
