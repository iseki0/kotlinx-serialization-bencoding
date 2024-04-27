package space.iseki.bencoding

import kotlinx.serialization.encoding.Decoder

interface BencodeDecoder : Decoder {
    fun decodeByteArray(): ByteArray
}
