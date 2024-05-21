package space.iseki.bencoding

import org.junit.Test
import space.iseki.bencoding.internal.InputStreamLexer
import space.iseki.bencoding.internal.Lexer

class InputStreamLexerTest{

    @Test
    fun test0(){
        val input = "ldei1234ei-1234ei0e0:0:1:a4:abcdi0e".byteInputStream()
        InputStreamLexer(input).debugPrintTokens()
    }
}

internal fun Lexer.debugPrintTokens(){
    var indent = 0
    while (true){
        val l = la()
        val lName = Lexer.name(l)
        val indentText = { " ".repeat(indent) }
        when(l){
            Lexer.DICT, Lexer.LIST -> {
                println(lName.prependIndent(indentText()))
                indent++
            }
            Lexer.END -> {
                indent--
                println(lName.prependIndent(indentText()))
            }
            Lexer.INTEGER ->{
                val i = nextInteger()
                println("$lName: $i".prependIndent(indentText()))
                continue
            }
            Lexer.STRING ->{
                val s = nextString()
                println("$lName: $s".prependIndent(indentText()))
                continue
            }
            Lexer.EOF -> {
                println("EOF")
                break
            }
        }
        skipToken()
    }
}
