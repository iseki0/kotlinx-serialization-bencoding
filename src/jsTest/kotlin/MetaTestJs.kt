import space.iseki.bencoding.Bencode
import space.iseki.bencoding.Meta
import kotlin.test.Test

class MetaTestJs {
    @Test
    fun testAndPrint() {
        val meta = Bencode.decodeFromByteArray<Meta>(Meta.serializer(), Meta.sampleTorrent)
        println(JSON.stringify(meta))
        js("console.log(String(meta))")
        println(getMethod(meta).joinToString("\n"))
    }

    private fun getMethod(v: dynamic): List<String> {
        var r = emptyArray<String>()
        js(
            """
            function getMethods(obj) {
              var result = [];
              for (var id in obj) {
                try {
                  if (typeof(obj[id]) == "function") {
                    result.push(id + ": " + String(obj[id]));
                  }
                } catch (err) {
                  result.push(id + ": inaccessible");
                }
              }
              return result;
            }
            r = getMethods(v);
        """
        )
        return r.toList()
    }
}