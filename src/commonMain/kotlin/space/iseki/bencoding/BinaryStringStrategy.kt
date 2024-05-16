package space.iseki.bencoding

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
