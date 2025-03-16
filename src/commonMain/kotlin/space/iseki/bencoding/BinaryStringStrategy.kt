package space.iseki.bencoding

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Represent the strategy to handle the binary data in [String].
 *
 * Because the bencoding standard and the serialization framework does not support [ByteArray], we convert it to [String] when decoding/encoding.
 * But many platforms haven't a standard way to store binary data in [String], so we provide some strategies to handle it.
 */
enum class BinaryStringStrategy {
    /**
     * Use ISO-8859-1 to encode/decode the binary data.
     */
    @Deprecated(message = "Use another name", replaceWith = ReplaceWith("Raw"))
    ISO88591,

    /**
     * Use Base64 to encode/decode the binary data.
     */
    Base64,

    /**
     * Use the default strategy that configured in the [BencodeOptions].
     */
    Default,

    /**
     * Encode every [Byte] into [Char] directly, so all character must be in code point `0..255`.
     */
    Raw, ;

    @OptIn(ExperimentalEncodingApi::class)
    internal fun decodeString(decoder: BencodeDecoder, strategy: BinaryStringStrategy) = work(
        strategy = strategy,
        options = decoder.options,
        base64 = { kotlin.io.encoding.Base64.encode(decoder.decodeByteArray()) },
        raw = { bytes2StringIso88591(decoder.decodeByteArray()) },
    )

    @OptIn(ExperimentalEncodingApi::class)
    internal fun encodeString(encoder: BencodeEncoder, strategy: BinaryStringStrategy, value: String) {
        work(
            strategy = strategy,
            options = encoder.options,
            base64 = { encoder.encodeByteArray(kotlin.io.encoding.Base64.decode(value)) },
            raw = { encoder.encodeByteArray(encodeRaw(value)) },
        )
    }

    @OptIn(ExperimentalEncodingApi::class)
    internal fun encodeString(
        encoder: BencodeCompositeEncoder,
        strategy: BinaryStringStrategy,
        descriptor: SerialDescriptor,
        index: Int,
        value: String,
    ) {
        work(
            strategy = strategy,
            options = encoder.options,
            base64 = { encoder.encodeByteArrayElement(descriptor, index, kotlin.io.encoding.Base64.decode(value)) },
            raw = { encoder.encodeByteArrayElement(descriptor, index, encodeRaw(value)) },
        )
    }

    @Suppress("DEPRECATION")
    private inline fun <T> work(
        strategy: BinaryStringStrategy,
        options: BencodeOptions,
        base64: () -> T,
        raw: () -> T,
    ): T = when (if (strategy == Default) options.binaryStringStrategy else strategy) {
        Raw, Default, ISO88591 -> raw()
        Base64 -> base64()
    }

    private fun encodeRaw(value: String): ByteArray {
        val bytes = ByteArray(value.length)
        for (i in value.indices) {
            val c = value[i].code
            if (c !in 0..255) throw IllegalArgumentException("Character at pos $i is not in 0..255")
            bytes[i] = c.toByte()
        }
        return bytes
    }

    private fun bytes2StringIso88591(bytes: ByteArray): String {
        val chars = CharArray(bytes.size)
        for (i in bytes.indices) {
            chars[i] = (bytes[i].toInt() and 0xff).toChar()
        }
        return chars.concatToString()
    }
}
