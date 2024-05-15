@file:JvmName("-Utils")

package space.iseki.bencoding

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor

internal expect fun bytes2Long(bytes: ByteArray, off: Int, len: Int): Long
internal expect fun bytes2StringIso88591(bytes: ByteArray, off: Int = 0, len: Int = bytes.size): String
internal expect fun string2BytesIso88591(s: String): ByteArray

@OptIn(ExperimentalSerializationApi::class)
internal fun SerialDescriptor.binaryStringAnnotation(childIndex: Int) =
    getElementAnnotations(childIndex).firstOrNull { it is BinaryString } as BinaryString?
