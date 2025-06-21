package hairinne.utils

object Unicodes {
    fun Int.toHexString(): String = Integer.toHexString(this)

    //Char -> Unicode
    fun encode(char: Char) = char.code.toHexString()

    // String -> Unicode
    fun encode(text: String) = text
        .toCharArray()
        .joinToString(separator = "", truncated = "") {
            encode(it)
        }.toInt()

    // Unicode -> String
    fun decode(codePoint: Int): CharArray = Character.toChars(codePoint)
}
