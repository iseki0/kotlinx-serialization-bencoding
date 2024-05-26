package space.iseki.bencoding.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import space.iseki.bencoding.BinaryString
import kotlin.math.max

internal expect fun bytes2Long(bytes: ByteArray, off: Int, len: Int): Long
internal expect fun bytes2StringIso88591(bytes: ByteArray, off: Int = 0, len: Int = bytes.size): String?

@OptIn(ExperimentalSerializationApi::class)
internal fun SerialDescriptor.binaryStringAnnotation(childIndex: Int) =
    getElementAnnotations(childIndex).firstOrNull { it is BinaryString } as BinaryString?

internal expect fun createBytesWriter0(): BWriter
internal fun createBytesWriter(): BWriter = createBytesWriter0()

