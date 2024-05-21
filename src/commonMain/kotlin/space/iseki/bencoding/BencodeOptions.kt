package space.iseki.bencoding

interface BencodeOptions {
    val floatStrategy: FloatNumberStrategy
    val doubleStrategy: FloatNumberStrategy
    val binaryStringStrategy: BinaryStringStrategy
}

