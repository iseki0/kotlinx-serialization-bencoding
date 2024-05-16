@file:JvmName(" FloatNumberStrategy")

package space.iseki.bencoding

import kotlinx.serialization.descriptors.SerialDescriptor

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

    context(BencodeDecoder)
    internal fun decodeDouble(): Double {
        return when (this) {
            Disallow -> reportError("Double number is not allowed")
            Rounded -> decodeLong().toDouble()
            IntegerIEEE754 -> Double.fromBits(decodeLong())
            DecimalString -> decodeString().toDouble()
        }
    }

    context(BencodeCompositeDecoder)
    internal fun decodeDouble(descriptor: SerialDescriptor, index: Int): Double {
        return when (this) {
            Disallow -> reportError("Double number is not allowed", descriptor, index)
            Rounded -> decodeLongElement(descriptor, index).toDouble()
            IntegerIEEE754 -> Double.fromBits(decodeLongElement(descriptor, index))
            DecimalString -> decodeStringElement(descriptor, index).toDouble()
        }
    }

    context(BencodeDecoder)
    internal fun decodeFloat(): Float {
        return when (this) {
            Disallow -> reportError("Float number is not allowed")
            Rounded -> decodeLong().toFloat()
            IntegerIEEE754 -> Float.fromBits(decodeInt())
            DecimalString -> decodeString().toFloat()
        }
    }

    context(BencodeCompositeDecoder)
    internal fun decodeFloat(descriptor: SerialDescriptor, index: Int): Float {
        return when (this) {
            Disallow -> reportError("Float number is not allowed", descriptor, index)
            Rounded -> decodeLongElement(descriptor, index).toFloat()
            IntegerIEEE754 -> Float.fromBits(decodeIntElement(descriptor, index))
            DecimalString -> decodeStringElement(descriptor, index).toFloat()
        }
    }

    context(BencodeEncoder)
    internal fun encodeDouble(value: Double) {
        when (this) {
            Disallow -> reportError("Double number is not allowed")
            Rounded -> encodeLong(value.toLong())
            IntegerIEEE754 -> encodeLong(value.toRawBits())
            DecimalString -> encodeString(value.toString())
        }
    }

    context(BencodeCompositeEncoder)
    internal fun encodeDouble(descriptor: SerialDescriptor, index: Int, value: Double) {
        when (this) {
            Disallow -> reportError("Double number is not allowed", descriptor, index)
            Rounded -> encodeLongElement(descriptor, index, value.toLong())
            IntegerIEEE754 -> encodeLongElement(descriptor, index, value.toRawBits())
            DecimalString -> encodeStringElement(descriptor, index, value.toString())
        }
    }

    context(BencodeEncoder)
    internal fun encodeFloat(value: Float) {
        when (this) {
            Disallow -> reportError("Float number is not allowed")
            Rounded -> encodeLong(value.toLong())
            IntegerIEEE754 -> encodeInt(value.toRawBits())
            DecimalString -> encodeString(value.toString())
        }
    }

    context(BencodeCompositeEncoder)
    internal fun encodeFloat(descriptor: SerialDescriptor, index: Int, value: Float) {
        when (this) {
            Disallow -> reportError("Float number is not allowed", descriptor, index)
            Rounded -> encodeLongElement(descriptor, index, value.toLong())
            IntegerIEEE754 -> encodeIntElement(descriptor, index, value.toRawBits())
            DecimalString -> encodeStringElement(descriptor, index, value.toString())
        }
    }
}

