package space.iseki.bencoding

import kotlinx.serialization.encoding.Encoder

interface BencodeEncoder : Encoder {
    val options: BencodeOptions
    fun encodeByteArray(bytes: ByteArray)
    fun encodeBinaryString(strategy: BinaryStringStrategy, value: String)
    fun reportError(message: String): Nothing = throw BencodeEncodeException(message)
}

