package space.iseki.bencoding

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.js.ExperimentalJsExport

@OptIn(ExperimentalJsExport::class)
@Serializable
data class Meta(
    val announce: String = "",
    @SerialName("announce-list") val announceList: List<List<String>> = emptyList(),
    val info: Info,
) {
    @Serializable
    data class Info(
        val name: String,
        @SerialName("piece length") val pieceLength: Int,
        @BinaryString(BinaryStringStrategy.ISO88591) val pieces: String,
    ) {
        override fun toString(): String {
            return "Info(name='$name', pieceLength=$pieceLength, pieces=(hash)${pieces.hashCode()})"
        }
    }
    companion object {

        @OptIn(ExperimentalEncodingApi::class)
        val sampleTorrent = """
        ZDg6YW5ub3VuY2U0MDpodHRwOi8vb3Blbi5hY2d0cmFja2VyLmNvbToxMDk2L2Fubm91bmNlMTM6
        YW5ub3VuY2UtbGlzdGxsNDA6aHR0cDovL29wZW4uYWNndHJhY2tlci5jb206MTA5Ni9hbm5vdW5j
        ZWVsMzY6aHR0cDovL29wZW50cmFja2VyLmFjZ254LnNlL2Fubm91bmNlZWw0NDp1ZHA6Ly90cmFj
        a2VyLm9wZW5iaXR0b3JyZW50LmNvbTo4MC9hbm5vdW5jZWVsMzg6dWRwOi8vdHJhY2tlci5wdWJs
        aWNidC5jb206ODAvYW5ub3VuY2VlbDMwOmh0dHA6Ly9zaGFyZS5kbWh5Lm9yZy9hbm5vbnVjZWVs
        MzY6aHR0cDovL255YWEudHJhY2tlci53Zjo3Nzc3L2Fubm91bmNlZWwzMTpodHRwOi8vYW5pZGV4
        Lm1vZTo2OTY5L2Fubm91bmNlZWwzODpodHRwOi8vdHJhY2tlci5hbmlyZW5hLmNvbTo4MC9hbm5v
        dW5jZWVsNDM6dWRwOi8vdHJhY2tlci5jb3BwZXJzdXJmZXIudGs6Njk2OS9hbm5vdW5jZWVsMzU6
        dWRwOi8vdHJhY2tlci51dzAueHl6OjY5NjkvYW5ub3VuY2VlbDQyOnVkcDovL3RyYWNrZXIub3Bl
        bnRyYWNrci5vcmc6MTMzNy9hbm5vdW5jZWVsMzU6dWRwOi8vMTA0LjIzOC4xOTguMTg2OjgwMDAv
        YW5ub251Y2VlbDMzOnVkcDovL29wZW4uc3RlYWx0aC5zaTo4MC9hbm5vdW5jZWVsMzc6dWRwOi8v
        ZXhvZHVzLmRlc3luYy5jb206Njk2OS9hbm5vdW5jZWVsMzI6dWRwOi8vdHJhY2tlci5wcnEudG86
        ODAvYW5ub3VuY2VlbDM1OnVkcDovLzEwNC4yMzguMTk4LjE4Njo4MDAwL2Fubm91bmNlZWwzNjpo
        dHRwOi8vMTA0LjIzOC4xOTguMTg2OjgwMDAvYW5ub3VuY2VlbDI5Omh0dHA6Ly85NC4yMjguMTky
        Ljk4L2Fubm91bmNlZWwzNDpodHRwOi8vdHJhY2tlci5idGNha2UuY29tL2Fubm91bmNlZWwzNzpo
        dHRwOi8vdHJhY2tlci5rdHhwLmNvbTo2ODY4L2Fubm91bmNlZWwzNzpodHRwOi8vdHJhY2tlci5r
        dHhwLmNvbTo3MDcwL2Fubm91bmNlZWwzMzpodHRwOi8vYnQuc2Mtb2wuY29tOjI3MTAvYW5ub3Vu
        Y2VlbDM1Omh0dHA6Ly9idGZpbGUuc2RvLmNvbTo2OTYxL2Fubm91bmNlZWw0MDpodHRwczovL3Qt
        MTE1LnJoY2xvdWQuY29tL29ubHlfZm9yX3lsYnVkZWwzNTpodHRwOi8vdHIuYmFuZ3VtaS5tb2U6
        Njk2OS9hbm5vdW5jZWVsMzg6aHR0cDovL2V4b2R1cy5kZXN5bmMuY29tOjY5NjkvYW5ub3VuY2Vl
        bDM1OnVkcDovL2NvcHBlcnN1cmZlci50azo2OTY5L2Fubm91bmNlZWwzOTpodHRwOi8vdHJhY2tl
        cjMudG9ycmVudGluby5jb20vYW5ub3VuY2VlbDM5Omh0dHA6Ly90cmFja2VyMi50b3JyZW50aW5v
        LmNvbS9hbm5vdW5jZWVsMzY6dWRwOi8vb3Blbi5kZW1vbmlpLmNvbToxMzM3L2Fubm91bmNlZWwz
        MTp1ZHA6Ly90cmFja2VyLmV4LnVhOjgwL2Fubm91bmNlZWwyOTpodHRwOi8vcHVidC5uZXQ6Mjcx
        MC9hbm5vdW5jZWVsMzI6aHR0cDovL3RyYWNrZXIudGZpbGUubWUvYW5ub3VuY2VlbDQ0Omh0dHA6
        Ly9iaWdmb290MTk0Mi5zZWt0b3JpLm9yZzo2OTY5L2Fubm91bmNlZWwzMzpodHRwOi8vdC5ueWFh
        dHJhY2tlci5jb20vYW5ub3VuY2VlbDMyOnVkcDovL2J0LnNjLW9sLmNvbToyNzEwL2Fubm91bmNl
        ZWUxMDpjcmVhdGVkIGJ5MTM6dVRvcnJlbnQvMjIxMDEzOmNyZWF0aW9uIGRhdGVpMTY4NTY0NjMx
        MGU4OmVuY29kaW5nNTpVVEYtODQ6aW5mb2Q1OmZpbGVzbGQ2Omxlbmd0aGkyNjg3Mjg2OTBlNDpw
        YXRobDc5OltTa3ltb29uLVJhd3NdIE1haG91dHN1a2FpIG5vIFlvbWUgUzAyIC0gMDkgW1ZpdVRW
        XVtXRUItRExdWzEwODBwXVtBVkMgQUFDXS5tcDRlZWQ2Omxlbmd0aGkxNzMyZTQ6cGF0aGw2NDpb
        57mB6auU5Lit5paHIChBbm5vdGF0aW9uKV0g6a2U5rOV5L2/55qE5paw5aiYIOesrDLlraMgZXAg
        OSAuYXNzZWVkNjpsZW5ndGhpNDY2ODJlNDpwYXRobDUxOlvnuYHpq5TkuK3mloddIOmtlOazleS9
        v+eahOaWsOWomCDnrKwy5a2jIGVwIDkgLmFzc2VlZDY6bGVuZ3RoaTE1MDQzNTg0ZTQ6cGF0aGwx
        ODpXZWlSdWFuWWFIZWktMS50dGZlZWU0Om5hbWU4OTpbU2t5bW9vbi1SYXdzXSBNYWhvdXRzdWth
        aSBubyBZb21lIFMwMiAtIDA5IFtWaXVUVl1bV0VCLURMXVtDSFRdWzEwODBwXVtBVkMgQUFDXVtN
        UDQrQVNTXTEyOnBpZWNlIGxlbmd0aGk1MjQyODhlNjpwaWVjZXMxMDg0MDrQvQHzR57cpO0SXKDS
        YnQBStYNE9ZA909g6D1Iy1V0pAPpygYr3n0bxtMOoqDLLoKnyhrMuF1CPrAphKcD/QI3uvW1Se0I
        /51ISD+zz9jiYeOUXlzzeQH1DHtLug0jDPRdIQPZSPCfpaKG/OfGHcyQsPabtbkuyDzveKqSoQLS
        ozyUB8CcqWuLO5lEpS9CHc09M/PGSyVVYZ3Pv7wxnDvBD7nu5O5aV4K4nsdPmbEiv3ZBtUIYAzCa
        tHS6gRlM/DKS7Yysg9G3I/IFmETrfrozbpzCWku+QDwBibpvwd2f59PHO7AanRlq18hoJP1w9Dsl
        /CbhhsbwwMwQdk6rJ9jikuh1vC9G82cXJgDjG0U9P6MtUaNQl7PQ3m7/yQXEAwqKPXIohpHxiskc
        uxgoSVT9hQjgaLefw5miG1pZNOtmo+jOVPWPsQGEf2MR6hWGDquIXxk1LPbCMAWZL2dEwsgMWruD
        otmO66gKhnCddpfDLPKXDj3JEgS3qM02GmgH/S8E9Bef8MwWcMSBrz7Ps6Ndn9rpKbxUH+iWM8YZ
        Ge6pLiYyVKLgIqfwe1cxx7a8CMmEU4UcsMbRiGdkg9kUhbJmNwyPG0uP8EpnxpGjj/OaQaIBeGPF
        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

    """.trimIndent().replace("\n", "").let { Base64.decode(it) }
        val sampleTorrentChecksum = run { // checksum of sampleTorrent
            val data = sampleTorrent
            var checksum = 0
            for (i in data.indices) {
                checksum += data[i].toInt()
            }
            checksum
        }
    }
}
