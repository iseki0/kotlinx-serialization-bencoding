package space.iseki.bencoding

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlin.test.Test
import kotlin.test.assertEquals

class AnnounceListTest {
    companion object {
        private val list = listOf(
            listOf("udp://bt.sc-ol.com:2710/announce"),
            listOf("http://t.nyaatracker.com/announce"),
            listOf("http://bigfoot1942.sektori.org:6969/announce"),
            listOf("http://tracker.tfile.me/announce"),
        )
        private val data = """
            d
                13:announce-list
                l
                    l
                        32:udp://bt.sc-ol.com:2710/announce
                    e
                    l
                        33:http://t.nyaatracker.com/announce
                    e
                    l
                        44:http://bigfoot1942.sektori.org:6969/announce
                    e
                    l
                        32:http://tracker.tfile.me/announce
                    e
                e
            e
        """.trimIndent().replace(" ", "").replace("\n", "")

        @Serializable
        data class A(
            @SerialName("announce-list") val announceList: List<List<String>>,
        )
    }

    @Test
    fun testEncode() {
        val encoded = Bencode.encodeToByteArray(A(list)).decodeToString()
        assertEquals(data, encoded)
    }

    @Test
    fun testDecode() {
        val decoded = Bencode.decodeFromByteArray<A>(data.encodeToByteArray())
        assertEquals(list, decoded.announceList)
    }

}
