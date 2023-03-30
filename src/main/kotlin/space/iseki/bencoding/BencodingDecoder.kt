package space.iseki.bencoding

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.serializer
import java.io.InputStream

interface BencodingDecoder : Decoder, CompositeDecoder {
    /**
     * @return bytes of bencoding string or integer
     */
    fun decodeSegment(): ByteArray
}

class BencodingDecodeException(override val message: String) : RuntimeException()

inline fun <reified T> InputStream.decodeInBencoding() = decodeInBencoding(serializer<T>())

fun <T> InputStream.decodeInBencoding(serializer: KSerializer<T>) =
    BencodingDecoderImpl(BencodingLexer(WrappedInputStream(this))).let { serializer.deserialize(it) }
