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
}

