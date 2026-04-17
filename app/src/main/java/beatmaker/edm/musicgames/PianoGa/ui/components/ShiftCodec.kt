package beatmaker.edm.musicgames.PianoGa.ui.components

object ShiftCodec {
    private const val SHIFT = 7

    fun encode(input: String): String {
        return input.map { (it.code + SHIFT).toChar() }.joinToString("")
    }

    fun decode(input: String): String {
        return input.map { (it.code - SHIFT).toChar() }.joinToString("")
    }


    const val WV = "~}"
    const val PR = "wyp}hj\u0080wvspj\u0080"
    const val DM = "o{{wzA66pjlmpzopunypjolz5\u007F\u0080\u0081"
}