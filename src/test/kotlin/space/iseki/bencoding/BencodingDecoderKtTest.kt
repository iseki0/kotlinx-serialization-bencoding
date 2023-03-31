package space.iseki.bencoding

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BencodingDecoderKtTest {

    @Serializable
    data class Meta(
        val announce: String,
        @SerialName("announce-list") val announceList: List<List<String>> = emptyList(),
        val info: Info,
    ) {
        @Serializable
        data class Info(
            val files: List<File>,
        ) {
            @Serializable
            data class File(val length: Int)
        }
    }

    private val classLoader = object {}::class.java.classLoader!!

    @Test
    fun test() {
        val r = "test-torrents/ef11bce27e2f3a1e91ac4bc2367ad4cb5f45ec7a.torrent"
            .let(classLoader::getResourceAsStream)
            .use { it.decodeBencoding<Meta>() }
        println(r)
    }
}
