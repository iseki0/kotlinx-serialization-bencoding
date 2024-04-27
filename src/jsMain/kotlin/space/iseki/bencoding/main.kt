package space.iseki.bencoding

import kotlinx.serialization.KSerializer

@OptIn(ExperimentalJsExport::class)
@JsExport
fun decodeUint8Array(blob: ByteArray, serializer: dynamic): dynamic {
    check(serializer is KSerializer<*>) { "serializer must be a KSerializer" }
    return Bencode.decodeFromByteArray(serializer, blob)
}
