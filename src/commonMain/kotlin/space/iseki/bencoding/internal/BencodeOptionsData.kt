package space.iseki.bencoding.internal

import space.iseki.bencoding.BencodeOptions
import space.iseki.bencoding.BinaryStringStrategy
import space.iseki.bencoding.FloatNumberStrategy

internal data class BencodeOptionsData(
    override val floatStrategy: FloatNumberStrategy,
    override val doubleStrategy: FloatNumberStrategy,
    override val binaryStringStrategy: BinaryStringStrategy,
) : BencodeOptions