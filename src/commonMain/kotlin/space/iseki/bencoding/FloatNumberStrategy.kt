@file:JvmName(" FloatNumberStrategy")

package space.iseki.bencoding

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.jvm.JvmName

/**
 * Describe how to handle the [Double] and [Float] number, which is not supported in the bencoding standard.
 */
enum class FloatNumberStrategy {
    /**
     * Disallow the [Double] and [Float] number, throw an exception when meet them.
     */
    Disallow,

    /**
     * Allow the [Double] and [Float] number, but convert them to integer when encoding/decoding.
     */
    Rounded,

    /**
     * Allow the [Double] and [Float] number, but convert them to integer that following IEEE754 standard when encoding/decoding.
     **
     * @see Double.toRawBits
     */
    IntegerIEEE754,

    /**
     * Allow the [Double] and [Float] number, but convert them to decimal-string when encoding/decoding.
     */
    DecimalString, ;

    internal fun decodeDouble(decoder: BencodeDecoder): Double {
        return when (this) {
            Disallow -> decoder.reportError("Double number is not allowed")
            Rounded -> decoder.decodeLong().toDouble()
            IntegerIEEE754 -> Double.fromBits(decoder.decodeLong())
            DecimalString -> decoder.decodeString().toDouble()
        }
    }

    internal fun decodeDouble(decoder: BencodeCompositeDecoder, descriptor: SerialDescriptor, index: Int): Double {
        return when (this) {
            Disallow -> decoder.reportError("Double number is not allowed", descriptor, index)
            Rounded -> decoder.decodeLongElement(descriptor, index).toDouble()
            IntegerIEEE754 -> Double.fromBits(decoder.decodeLongElement(descriptor, index))
            DecimalString -> decoder.decodeStringElement(descriptor, index).toDouble()
        }
    }

    internal fun decodeFloat(decoder: BencodeDecoder): Float {
        return when (this) {
            Disallow -> decoder.reportError("Float number is not allowed")
            Rounded -> decoder.decodeLong().toFloat()
            IntegerIEEE754 -> Float.fromBits(decoder.decodeInt())
            DecimalString -> decoder.decodeString().toFloat()
        }
    }

    internal fun decodeFloat(decoder: BencodeCompositeDecoder, descriptor: SerialDescriptor, index: Int): Float {
        return when (this) {
            Disallow -> decoder.reportError("Float number is not allowed", descriptor, index)
            Rounded -> decoder.decodeLongElement(descriptor, index).toFloat()
            IntegerIEEE754 -> Float.fromBits(decoder.decodeIntElement(descriptor, index))
            DecimalString -> decoder.decodeStringElement(descriptor, index).toFloat()
        }
    }

    internal fun encodeDouble(encoder: BencodeEncoder, value: Double) {
        when (this) {
            Disallow -> encoder.reportError("Double number is not allowed")
            Rounded -> encoder.encodeLong(value.toLong())
            IntegerIEEE754 -> encoder.encodeLong(value.toRawBits())
            DecimalString -> encoder.encodeString(value.toString())
        }
    }

    internal fun encodeDouble(
        encoder: BencodeCompositeEncoder,
        descriptor: SerialDescriptor,
        index: Int,
        value: Double,
    ) {
        when (this) {
            Disallow -> encoder.reportError("Double number is not allowed", descriptor, index)
            Rounded -> encoder.encodeLongElement(descriptor, index, value.toLong())
            IntegerIEEE754 -> encoder.encodeLongElement(descriptor, index, value.toRawBits())
            DecimalString -> encoder.encodeStringElement(descriptor, index, value.toString())
        }
    }

    internal fun encodeFloat(encoder: BencodeEncoder, value: Float) {
        when (this) {
            Disallow -> encoder.reportError("Float number is not allowed")
            Rounded -> encoder.encodeLong(value.toLong())
            IntegerIEEE754 -> encoder.encodeInt(value.toRawBits())
            DecimalString -> encoder.encodeString(value.toString())
        }
    }

    internal fun encodeFloat(encoder: BencodeCompositeEncoder, descriptor: SerialDescriptor, index: Int, value: Float) {
        when (this) {
            Disallow -> encoder.reportError("Float number is not allowed", descriptor, index)
            Rounded -> encoder.encodeLongElement(descriptor, index, value.toLong())
            IntegerIEEE754 -> encoder.encodeIntElement(descriptor, index, value.toRawBits())
            DecimalString -> encoder.encodeStringElement(descriptor, index, value.toString())
        }
    }
}

