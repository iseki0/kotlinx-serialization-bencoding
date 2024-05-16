package space.iseki.bencoding

import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlin.test.Test
import kotlin.test.assertEquals

class SampleTest {

    val data = "d2:abli-33ee3:abcli20eee"
    val dataMapped = mapOf(
        "abc" to listOf(20),
        "ab" to listOf(-33),
    )

    @Test
    fun testDecode() {
        val decoded = Bencode.decodeFromByteArray<Map<String, List<Int>>>(data.encodeToByteArray())
        assertEquals(dataMapped, decoded)
    }

    @Test
    fun testEncode() {
        val encoded = Bencode.encodeToByteArray(dataMapped)
        assertEquals(data, encoded.decodeToString())
    }
}
