@file:JvmName(" Bencode")

package space.iseki.bencoding

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

interface Bencode : BinaryFormat {
    val options: BencodeOptions

    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
        BencodeDecoder0(BytesLexer(bytes), serializersModule, options).decodeSerializableValue(deserializer)

    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
        TODO("Not yet implemented")
    }

    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()


    companion object : Bencode {
        private val defaultOptions = BencodeConfigureScope0().build()
        override val options: BencodeOptions
            get() = defaultOptions
    }

}

fun Bencode(configure: BencodeConfigureScope.() -> Unit): Bencode = object : Bencode {
    override val options: BencodeOptions = BencodeConfigureScope0().apply(configure).build()
}


interface BencodeConfigureScope {
    var floatStrategy: FloatNumberStrategy
    var doubleStrategy: FloatNumberStrategy
    var binaryStringStrategy: BinaryStringStrategy
}

private class BencodeConfigureScope0 : BencodeConfigureScope {
    override var floatStrategy: FloatNumberStrategy = FloatNumberStrategy.Disallow
    override var doubleStrategy: FloatNumberStrategy = FloatNumberStrategy.Disallow
    override var binaryStringStrategy: BinaryStringStrategy = BinaryStringStrategy.ISO88591
    fun build() = BencodeOptionsData(floatStrategy, doubleStrategy, binaryStringStrategy)
}

