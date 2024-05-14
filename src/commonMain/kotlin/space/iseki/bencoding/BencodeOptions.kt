package space.iseki.bencoding

interface BencodeOptions {
    val floatStrategy: FloatNumberStrategy
    val doubleStrategy: FloatNumberStrategy
}

internal data class BencodeOptionsData(
    override val floatStrategy: FloatNumberStrategy = FloatNumberStrategy.Disallow,
    override val doubleStrategy: FloatNumberStrategy = FloatNumberStrategy.Disallow,
) : BencodeOptions

