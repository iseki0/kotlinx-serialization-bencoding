package space.iseki.bencoding

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
internal class BencodeEncoder0(
    override val serializersModule: SerializersModule,
    override val options: BencodeOptions,
    val writer: BWriter,
) : EncoderDelegate {

    override fun encodeValue(value: EncodeValue) {
        when (value) {
            is EncodeValue.Number -> writer.writeInt(value.v)
            is EncodeValue.Text -> writer.writeString(value.v)
            is EncodeValue.Bytes -> writer.writeByteArray(value.v)
            is EncodeValue.Serialized<*> -> value.doEncode(this)
        }
    }

    private class Entry(val k: String, val v: EncodeValue)

    internal inner class MapEncoder : CompositeEncoderDelegate {
        override val rootRef: BencodeEncoder
            get() = this@BencodeEncoder0
        private val values = arrayListOf<Entry>()
        private var key: String? = null

        override fun encodeValueElement(descriptor: SerialDescriptor, index: Int, value: EncodeValue) {
            if (key == null) {
                // encode key
                when (value) {
                    is EncodeValue.Text -> key = value.v
                    is EncodeValue.Serialized<*> -> {
                        if (value.serializer.descriptor.kind != PrimitiveKind.STRING) {
                            reportError("key must be string", descriptor, index)
                        }
                        key = extractKeyStringFromSerializedValue(descriptor, index, value)
                    }

                    else -> reportError("key must be string", descriptor, index)
                }
                return
            }
            val key = key ?: reportError("key hasn't encoded yet", descriptor, index)
            values.add(Entry(key, value))
            this.key = null
        }

        override fun endStructure(descriptor: SerialDescriptor) {
            values.sortBy { it.k }
            for (it in values) {
                writer.writeString(it.k)
                this@BencodeEncoder0.encodeValue(it.v)
            }
            writer.writeEnd()
        }

        private fun extractKeyStringFromSerializedValue(
            descriptor: SerialDescriptor, index: Int, value: EncodeValue.Serialized<*>
        ): String {
            val encoder = MapKeyExtractorEncoder(this@BencodeEncoder0, descriptor, index)
            value.doEncode(encoder)
            return encoder.key ?: reportError("no key encoded")
        }
    }

    internal inner class ClassEncoder : CompositeEncoderDelegate {
        override val rootRef: BencodeEncoder
            get() = this@BencodeEncoder0
        private val values = arrayListOf<Entry>()

        override fun encodeValueElement(descriptor: SerialDescriptor, index: Int, value: EncodeValue) {
            values.add(Entry(descriptor.getElementName(index), value))
        }

        override fun endStructure(descriptor: SerialDescriptor) {
            values.sortBy { it.k }
            for (it in values) {
                writer.writeString(it.k)
                this@BencodeEncoder0.encodeValue(it.v)
            }
            writer.writeEnd()
        }
    }

    internal inner class ListEncoder : CompositeEncoderDelegate {
        override val rootRef: BencodeEncoder
            get() = this@BencodeEncoder0
        override val encodeToRootDirectly: Boolean
            get() = true

        override fun encodeValueElement(descriptor: SerialDescriptor, index: Int, value: EncodeValue) {
            throw Error("unreachable")
        }

        override fun endStructure(descriptor: SerialDescriptor) {
            writer.writeEnd()
        }

    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return when (val kind = descriptor.kind) {
            StructureKind.LIST -> ListEncoder().also { writer.writeListBegin() }
            StructureKind.OBJECT, StructureKind.CLASS -> ClassEncoder().also { writer.writeDictBegin() }
            StructureKind.MAP -> MapEncoder().also { writer.writeDictBegin() }
            else -> reportError("unsupported kind: $kind, in descriptor: ${descriptor.serialName}")
        }
    }

    override val rootRef: BencodeEncoder
        get() = this

}

