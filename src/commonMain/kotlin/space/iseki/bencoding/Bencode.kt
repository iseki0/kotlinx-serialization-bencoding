package space.iseki.bencoding

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

interface Bencode: BinaryFormat {
    companion object: Bencode {
        override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
            BencodeDecoder0(BytesLexer(bytes), serializersModule).decodeSerializableValue(deserializer)

        override val serializersModule: SerializersModule
            get() = EmptySerializersModule()

        override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
            TODO("Not yet implemented")
        }
    }
}
