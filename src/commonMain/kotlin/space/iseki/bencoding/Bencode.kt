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
        private val defaultOptions = BencodeOptionsData()
        override val options: BencodeOptions
            get() = defaultOptions
    }

}

fun Bencode(configure: BencodeConfigureScope.() -> Unit): Bencode {
    val scope = object : BencodeConfigureScope {
        override var floatStrategy: FloatNumberStrategy = FloatNumberStrategy.Disallow
        override var doubleStrategy: FloatNumberStrategy = FloatNumberStrategy.Disallow
    }
    scope.configure()
    return object : Bencode {
        override val options: BencodeOptions = BencodeOptionsData(scope.floatStrategy, scope.doubleStrategy)
    }
}


interface BencodeConfigureScope {
    var floatStrategy: FloatNumberStrategy
    var doubleStrategy: FloatNumberStrategy
}


