package space.iseki.bencoding.internal

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule
import space.iseki.bencoding.BencodeCompositeDecoder
import space.iseki.bencoding.BencodeDecodeException
import space.iseki.bencoding.BencodeDecoder
import space.iseki.bencoding.BencodeOptions
import space.iseki.bencoding.BinaryStringStrategy

@OptIn(ExperimentalSerializationApi::class)
internal class BencodeDecoder0(
    private val lexer: Lexer,
    override val serializersModule: SerializersModule,
    override val options: BencodeOptions,
) : BencodeDecoder, BencodeCompositeDecoder {

    private fun unsupported(kind: String): Nothing = throw BencodeDecodeException(
        lexer.pos(), "$kind is not supported"
    )

    @OptIn(ExperimentalSerializationApi::class)
    private fun unsupported(sd: SerialDescriptor, i: Int): Nothing {
        throw BencodeDecodeException(lexer.pos(), "type of ${sd.serialName}/${sd.getElementName(i)} is not supported")
    }

    private inline fun <R> el(descriptor: SerialDescriptor, index: Int, f: () -> R): R {
        m.g(descriptor, index)
        return f()
    }

    private val m = M()
    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean =
        unsupported(descriptor, index)

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float =
        options.floatStrategy.decodeFloat(descriptor, index)

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double =
        options.doubleStrategy.decodeDouble(descriptor, index)

    override fun decodeByteArrayElement(descriptor: SerialDescriptor, index: Int) =
        el(descriptor, index, ::decodeByteArray)

    override fun reportError(message: String, descriptor: SerialDescriptor, index: Int): Nothing {
        throw BencodeDecodeException(
            lexer.pos(), "$message, at ${descriptor.serialName}/${descriptor.getElementName(index)}"
        )
    }

    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int) = el(descriptor, index, ::decodeByte)
    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int) = el(descriptor, index, ::decodeChar)
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = m.d(descriptor)
    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int) = el(descriptor, index) { this }
    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int) = el(descriptor, index, ::decodeInt)
    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int) = el(descriptor, index, ::decodeLong)
    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?,
    ): T? = el(descriptor, index) { deserializer.deserialize(this) }

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?,
    ): T = el(descriptor, index) { deserializer.deserialize(this) }

    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int) = el(descriptor, index, ::decodeShort)
    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int) = el(descriptor, index) {
        val anno = descriptor.binaryStringAnnotation(index)
        if (anno != null) {
            decodeBinaryString(anno.strategy)
        } else {
            decodeString()
        }
    }

    override fun endStructure(descriptor: SerialDescriptor) = m.end()
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = apply { m.begin(descriptor) }
    override fun decodeBoolean(): Boolean = unsupported("boolean")
    override fun decodeByte(): Byte = decodeInt().toByte()
    override fun decodeChar(): Char = decodeInt().toChar()
    override fun decodeDouble(): Double = options.doubleStrategy.decodeDouble()
    override fun decodeEnum(enumDescriptor: SerialDescriptor) = enumDescriptor.getElementIndex(decodeString())
    override fun decodeFloat(): Float = options.floatStrategy.decodeFloat()
    override fun decodeInline(descriptor: SerialDescriptor): Decoder = this
    override fun decodeInt(): Int = lexer.nextInteger().toInt()
    override fun decodeLong(): Long = lexer.nextInteger()
    override fun decodeNotNullMark(): Boolean = false
    override fun decodeNull(): Nothing? = null
    override fun decodeShort(): Short = decodeInt().toShort()
    override fun decodeString(): String = lexer.nextBytes().decodeToString()
    override fun decodeBinaryString(strategy: BinaryStringStrategy): String =
        options.binaryStringStrategy.decodeString(strategy)

    override fun reportError(message: String): Nothing {
        throw BencodeDecodeException(lexer.pos(), message)
    }

    override fun decodeByteArray(): ByteArray = lexer.nextBytes()

    companion object {
        // Base constants used for bit shifting
        private const val C_READ = 1
        private const val T_LIST = 1 shl 1
        private const val T_MAP_KEY = 1 shl 2
        private const val T_MAP_VAL = 1 shl 3
        private const val T_OBJ = 1 shl 4

        // LIST related constants
        const val LIST_IDLE = T_LIST
        const val LIST_READ = T_LIST or C_READ

        // MAP_KEY related constants
        const val MAP_KEY_IDLE = T_MAP_KEY
        const val MAP_KEY_READ = T_MAP_KEY or C_READ

        // MAP_VAL related constants
        const val MAP_VAL_IDLE = T_MAP_VAL
        const val MAP_VAL_READ = T_MAP_VAL or C_READ

        // OBJ related constants
        const val OBJ_IDLE = T_OBJ
        const val OBJ_READ = T_OBJ or C_READ

    }


    @OptIn(ExperimentalSerializationApi::class)
    private inner class M {

        private var stack = LongArray(0)
        private var sp = -1
        private fun ensureMoreSpace() {
            if (sp + 1 == stack.size) {
                stack = stack.copyInto(LongArray(stack.size.coerceAtLeast(2) * 2))
            }
        }

        private fun currIndex(): Int {
            val s = stack[sp]
            return s.toInt()
        }

        private fun currState(): Int {
            val s = stack[sp]
            return (s ushr 32).toInt()
        }

        private fun setIndex(index: Int) {
            stack[sp] = index.toLong() and 0xFFFFFFFFL or (stack[sp] and (0xffffffffL shl 32))
        }

        private fun setState(state: Int) {
            stack[sp] = (state.toLong() shl 32) or (stack[sp] and 0xFFFFFFFFL)
        }

        private fun pushState(state: Int) {
            ensureMoreSpace()
            stack[++sp] = state.toLong() shl 32
        }

        private fun laExpect(t: Int) {
            if (t != lexer.la()) {
                val exp = Lexer.name(t)
                val act = Lexer.name(lexer.la())
                throw BencodeDecodeException(lexer.pos(), "expect token $exp, but got $act")
            }
        }

        private fun laUnexpect(t: Int) {
            if (t == lexer.la()) {
                val act = Lexer.name(t)
                throw BencodeDecodeException(lexer.pos(), "unexpected token $act")
            }
        }

        private fun consumeExpect(t: Int) {
            laExpect(t)
            lexer.skipToken()
        }

        fun begin(d: SerialDescriptor) {
            when (val kind = d.kind) {
                StructureKind.LIST -> {
                    consumeExpect(Lexer.LIST)
                    pushState(LIST_IDLE)
                    setIndex(-1)
                }

                StructureKind.MAP -> {
                    consumeExpect(Lexer.DICT)
                    pushState(MAP_KEY_IDLE)
                }

                StructureKind.CLASS, StructureKind.OBJECT -> {
                    consumeExpect(Lexer.DICT)
                    pushState(OBJ_IDLE)
                }

                else -> reportError("unsupported structured descriptor: ${d.serialName}, kind: $kind")
            }
        }

        fun end() {
            check(sp >= 0) { "stack underflow" }
            while (lexer.la() != Lexer.END) {
                lexer.skipValue()
            }
            lexer.skipToken()
            sp--
        }

        fun g(sd: SerialDescriptor, i: Int) {
            if (currState() and C_READ == 0) reportError("not in read state", sd, i)
            if (currIndex() != i) reportError("bad index: $i, expect: ${currIndex()}", sd, i)
            setState(postReadStateOf(currState()))
        }

        private fun postReadStateOf(old: Int): Int {
            check(old and C_READ != 0)
            return when (old) {
                MAP_KEY_READ -> MAP_VAL_IDLE
                MAP_VAL_READ -> MAP_KEY_IDLE
                else -> old xor C_READ
            }
        }

        private fun goAround(sd: SerialDescriptor, state: Int): Int {
            setState(state)
            return d(sd)
        }

        fun d(sd: SerialDescriptor): Int {
            when (val s = currState()) {
                LIST_IDLE -> {
                    if (lexer.la() == Lexer.END) return CompositeDecoder.DECODE_DONE
                    laUnexpect(Lexer.EOF)
                    setState(LIST_READ)
                    setIndex(currIndex() + 1)
                    return currIndex()
                }

                LIST_READ -> {
                    lexer.skipValue()
                    return goAround(sd, LIST_IDLE)
                }

                MAP_KEY_IDLE -> {
                    if (lexer.la() == Lexer.END) return CompositeDecoder.DECODE_DONE
                    laExpect(Lexer.STRING)
                    setState(MAP_KEY_READ)
                    setIndex(currIndex() + 1)
                    return currIndex()
                }

                MAP_KEY_READ -> {
                    lexer.skipValue()
                    lexer.skipValue()
                    return goAround(sd, MAP_KEY_IDLE)
                }

                MAP_VAL_IDLE -> {
                    laUnexpect(Lexer.END)
                    laUnexpect(Lexer.EOF)
                    setState(MAP_VAL_READ)
                    setIndex(currIndex() + 1)
                    return currIndex()
                }

                MAP_VAL_READ -> {
                    lexer.skipValue()
                    return goAround(sd, MAP_KEY_IDLE)
                }

                OBJ_IDLE -> {
                    while (true) {
                        if (lexer.la() == Lexer.END) return CompositeDecoder.DECODE_DONE
                        laExpect(Lexer.STRING)
                        val index = sd.getElementIndex(lexer.nextBytes().decodeToString())
                        if (index == CompositeDecoder.UNKNOWN_NAME) {
                            lexer.skipValue()
                            continue
                        }
                        setIndex(index)
                        setState(OBJ_READ)
                        return index
                    }
                }

                else -> {
                    check(s >= 0) { "bad stack top: $s" }
                    lexer.skipValue()
                    return goAround(sd, OBJ_IDLE)
                }
            }
        }

    }

}