package space.iseki.bencoding

import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import space.iseki.bencoding.internal.decodeFromStream
import space.iseki.bencoding.internal.encodeToStream
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BencodingStreamTest {
    private val data = Meta.sampleTorrent

    @Test
    fun testEncode() {
        val torrent = Bencode.decodeFromByteArray<Meta>(data)
        val a = ByteArrayOutputStream().also { Bencode.encodeToStream(torrent, it) }.toByteArray()
        val b = Bencode.encodeToByteArray(torrent)
        println(Json.encodeToString(a.decodeToString()))
        assertContentEquals(a, b)
    }

    @Test
    fun testDecode() {
        val a = Bencode.decodeFromStream<Meta>(data.inputStream()).also(::println)
        val b = Bencode.decodeFromByteArray<Meta>(data)
        assertEquals(a, b)
    }

}