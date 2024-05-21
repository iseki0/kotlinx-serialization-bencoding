package space.iseki.bencoding

import kotlinx.serialization.descriptors.SerialDescriptor
import space.iseki.bencoding.internal.bytes2StringIso88591
import space.iseki.bencoding.internal.string2BytesIso88591
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
    ISO88591,

    /**
     * Use Base64 to encode/decode the binary data.
     */
    Base64,

    /**
     * Use the default strategy that configured in the [BencodeOptions].
     */
    Default, ;

    context(BencodeDecoder)
    @OptIn(ExperimentalEncodingApi::class)
    internal fun decodeString(strategy: BinaryStringStrategy) = work(
        strategy = strategy,
        options = options,
        iso88591 = { bytes2StringIso88591(decodeByteArray()) },
        base64 = { kotlin.io.encoding.Base64.encode(decodeByteArray()) },
    )

    context(BencodeEncoder)
    @OptIn(ExperimentalEncodingApi::class)
    internal fun encodeString(strategy: BinaryStringStrategy, value: String) {
        work(
            strategy = strategy,
            options = options,
            iso88591 = { encodeByteArray(string2BytesIso88591(value)) },
            base64 = { encodeByteArray(kotlin.io.encoding.Base64.decode(value)) },
        )
    }

    context(BencodeCompositeEncoder)
    @OptIn(ExperimentalEncodingApi::class)
    internal fun encodeString(strategy: BinaryStringStrategy, descriptor: SerialDescriptor, index: Int, value: String) {
        work(
            strategy = strategy,
            options = options,
            iso88591 = { encodeByteArrayElement(descriptor, index, string2BytesIso88591(value)) },
            base64 = { encodeByteArrayElement(descriptor, index, kotlin.io.encoding.Base64.decode(value)) },
        )
    }

    private inline fun <T> work(
        strategy: BinaryStringStrategy,
        options: BencodeOptions,
        iso88591: () -> T,
        base64: () -> T,
    ): T = when (if (strategy == Default) options.binaryStringStrategy else strategy) {
        ISO88591, Default -> iso88591()
        Base64 -> base64()
    }
}
