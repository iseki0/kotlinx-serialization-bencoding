package space.iseki.bencoding

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder

interface BencodingEncoder : Encoder, CompositeEncoder {
    fun encodeInteger(number: Number)
    fun encodeText(data: ByteArray)
}

class BencodingEncodeException(override val message: String) : RuntimeException()

internal class BencodingEncoderImpl(
    private val output: Output,
) : BencodingEncoder, BencodingEncoderHelper {

    override fun endStructure(descriptor: SerialDescriptor) {
        TODO("Not yet implemented")
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        TODO("Not yet implemented")
    }

    override fun encodeInteger(number: Number) {
        output.write('i')
        output.write(number.toString().encodeToByteArray())
        output.write('e')
    }

    override fun encodeText(data: ByteArray) {
        output.write(data.size.toString().encodeToByteArray())
        output.write(':')
        output.write(data)
    }
}
