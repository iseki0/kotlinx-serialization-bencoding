package space.iseki.bencoding

import kotlinx.serialization.encoding.Decoder

interface BencodeDecoder : Decoder {
    fun decodeByteArray(): ByteArray
    fun decodeBinaryString(strategy: BinaryStringStrategy): String
    fun reportError(message: String): Nothing = throw BencodeDecodeException(-1, message)
    val options: BencodeOptions
}
