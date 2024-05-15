package space.iseki.bencoding

interface BencodeOptions {
    val floatStrategy: FloatNumberStrategy
    val doubleStrategy: FloatNumberStrategy
    val binaryStringStrategy: BinaryStringStrategy
}

internal data class BencodeOptionsData(
    override val floatStrategy: FloatNumberStrategy,
    override val doubleStrategy: FloatNumberStrategy,
    override val binaryStringStrategy: BinaryStringStrategy,
) : BencodeOptions

