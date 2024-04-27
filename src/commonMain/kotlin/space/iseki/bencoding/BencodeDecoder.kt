package space.iseki.bencoding

import kotlinx.serialization.encoding.Decoder

interface BencodeDecoder : Decoder {
    fun decodeByteArray(): ByteArray
    fun decodeStringIso88591(): String
}
