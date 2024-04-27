package space.iseki.bencoding

import kotlin.test.Test

class MetaTest {
    @Test
    fun sampleChecksum() {
        println(Meta.sampleTorrentChecksum)
        println(Meta.sampleTorrent.size)
    }

    @Test
    fun decodeTest() {
        val meta = Bencode.decodeFromByteArray<Meta>(Meta.serializer(), Meta.sampleTorrent)
        println(meta)
    }
}
