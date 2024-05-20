package space.iseki.bencoding

import kotlinx.serialization.decodeFromByteArray
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
}
