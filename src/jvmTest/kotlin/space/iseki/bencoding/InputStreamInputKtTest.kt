package space.iseki.bencoding

import kotlinx.serialization.decodeFromByteArray
import space.iseki.bencoding.internal.decodeFromStream
import kotlin.test.assertEquals

private val data = Meta.sampleTorrent

class InputStreamInputKtTest {

    //    @Test
    fun testDecoding() {
        val a = Bencode.decodeFromStream<Meta>(data.inputStream()).also(::println)
        val b = Bencode.decodeFromByteArray<Meta>(data)
        assertEquals(a, b)
    }
}

