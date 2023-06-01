package space.iseki.bencoding

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.test.Test

private val data = object {}::class.java.classLoader
    .getResourceAsStream("d2cb5a6c67221f9abc2b5e0aa75556d80d287f83.torrent")!!
    .use { it.readAllBytes() }

class InputStreamInputKtTest {

    @Test
    fun testDecoding() {
        data.inputStream().decodeInBencoding<Meta>().also(::println)
    }
}

@Serializable
@JvmRecord
data class Meta(
    val announce: String = "",
    @SerialName("announce-list") val announceList: List<List<String>> = emptyList(),
    val info: Info,
) {
    @Serializable
    data class Info(
        val name: String,
        @SerialName("piece length") val pieceLength: Int,
        val pieces: Pieces,
    )

    @Serializable(Pieces.Serializer::class)
    class Pieces(private val arr: ByteArray) {
        override fun toString(): String = "Pieces(size=${arr.size / 20})"
        override fun hashCode(): Int = arr.contentHashCode()
        override fun equals(other: Any?): Boolean = (other as? Pieces)?.let { it.arr.contentEquals(arr) } ?: false

        object Serializer : KSerializer<Pieces> {
            override val descriptor: SerialDescriptor
                get() = serialDescriptor<String>()

            override fun deserialize(decoder: Decoder): Pieces =
                (decoder as BencodingDecoder).decodeBytes().let(::Pieces)

            override fun serialize(encoder: Encoder, value: Pieces) {
                TODO("Not yet implemented")
            }

        }
    }
}
