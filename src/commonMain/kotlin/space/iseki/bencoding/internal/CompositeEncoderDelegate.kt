package space.iseki.bencoding.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import space.iseki.bencoding.BencodeCompositeEncoder
import space.iseki.bencoding.BencodeEncoder
import space.iseki.bencoding.BencodeOptions
import space.iseki.bencoding.BinaryStringStrategy

internal interface CompositeEncoderDelegate : BencodeCompositeEncoder {
    val rootRef: BencodeEncoder
    override val serializersModule: SerializersModule
        get() = rootRef.serializersModule
    override val options: BencodeOptions
        get() = rootRef.options

    // for the encoder which can be encoded directly, avoid boxing-cost
    val encodeToRootDirectly: Boolean
        get() = false

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        encodeLongElement(descriptor, index, value.code.toLong())
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        encodeLongElement(descriptor, index, value.toLong())
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        encodeLongElement(descriptor, index, value.toLong())
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        encodeLongElement(descriptor, index, value.toLong())
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        options.floatStrategy.encodeFloat(this, descriptor, index, value)
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        options.doubleStrategy.encodeDouble(this, descriptor, index, value)
    }

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        reportError("boolean is not supported", descriptor, index)
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) {
        if (encodeToRootDirectly) {
            rootRef.encodeSerializableValue(serializer, value)
        } else {
            encodeValueElement(descriptor, index, EncodeValue.Serialized(value, serializer))
        }
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        if (encodeToRootDirectly) {
            rootRef.encodeLong(value)
        } else {
            encodeValueElement(descriptor, index, EncodeValue.Number(value))
        }
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        val anno = descriptor.binaryStringAnnotation(index)
        if (anno == null) {
            if (encodeToRootDirectly) {
                rootRef.encodeString(value)
            } else {
                encodeValueElement(descriptor, index, EncodeValue.Text(value))
            }
        } else {
            encodeBinaryStringElement(descriptor, index, anno.strategy, value)
        }
    }

    override fun encodeByteArrayElement(descriptor: SerialDescriptor, index: Int, value: ByteArray) {
        if (encodeToRootDirectly) {
            rootRef.encodeByteArray(value)
        } else {
            encodeValueElement(descriptor, index, EncodeValue.Bytes(value))
        }
    }

    override fun encodeBinaryStringElement(
        descriptor: SerialDescriptor,
        index: Int,
        strategy: BinaryStringStrategy,
        value: String
    ) {
        options.binaryStringStrategy.encodeString(this, strategy, descriptor, index, value)
    }

    private fun throwForbiddenCallToBeginStructure(descriptor: SerialDescriptor, index: Int): Nothing {
        reportError(
            message = "call beginStructure(SerialDescriptor) in inline element is unsupported",
            descriptor = descriptor,
            index = index,
        )
    }

    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
        val _descriptor = descriptor
        if (encodeToRootDirectly) {
            return object : Encoder by rootRef {
                override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
                    throwForbiddenCallToBeginStructure(_descriptor, index)
                }
            }
        }
        return object : EncoderDelegate {
            override val rootRef: BencodeEncoder
                get() = this@CompositeEncoderDelegate.rootRef

            override fun encodeValue(value: EncodeValue) {
                encodeValueElement(_descriptor, index, value)
            }

            override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
                throwForbiddenCallToBeginStructure(_descriptor, index)
            }

        }
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) {
        encodeSerializableElement(
            descriptor,
            index,
            serializer,
            value ?: reportError("null is not supported", descriptor, index)
        )
    }

    fun encodeValueElement(descriptor: SerialDescriptor, index: Int, value: EncodeValue)
}
