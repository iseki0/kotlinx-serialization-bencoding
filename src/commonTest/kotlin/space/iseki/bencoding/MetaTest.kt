package space.iseki.bencoding

import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlin.test.Test
import kotlin.test.assertEquals

class MetaTest {
    @Test
    fun sampleChecksum() {
        println(Meta.sampleTorrentChecksum)
        println(Meta.sampleTorrent.size)
    }

    @Test
    fun decodeTest() {
        val bencode = Bencode {
            floatStrategy = FloatNumberStrategy.Disallow
            doubleStrategy = FloatNumberStrategy.Disallow
            binaryStringStrategy = BinaryStringStrategy.Base64 // but it's configured to ISO8859-1 on the pieces field
        }
        val meta = bencode.decodeFromByteArray(Meta.serializer(), Meta.sampleTorrent)
        println(meta.info.pieces.length)
        println(meta)
        assertEquals(0, meta.info.pieces.length % 20)
    }

    @Test
    @OptIn(ExperimentalStdlibApi::class)
    fun encodeTest() {
        val decodedMeta = Bencode.decodeFromByteArray(Meta.serializer(), Meta.sampleTorrent)
        val r = Bencode.encodeToByteArray(decodedMeta)
        println(r.toHexString())
        assertEquals(decodedMeta, Bencode.decodeFromByteArray<Meta>(r))
    }
}
