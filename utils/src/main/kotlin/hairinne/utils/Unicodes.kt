package hairinne.utils

object Unicodes {
    /*
      在 Kotlin 中,Char.code 返回的是 Int 类型, 表示该字符的 16 位 UTF-16 编码值 (即 UTF-16 code unit)
      对于 BMP (基本多语言平面) 中的字符(即 Unicode 码点范围为 U+0000 到 U+FFFF 的字符),
      UTF-16 编码与 Unicode 码点是一致的, 因此 Char.code 返回的值等价于其 Unicode 码点
      对于 辅助平面 (Supplemental Planes) 中的字符 (即 Unicode 码点范围为 U+10000 到 U+10FFFF 的字符),
      Kotlin 的 Char 无法单独表示这些字符, 它们需要用 代理对 (surrogate pair) 表示,
      此时单个 Char 的 code 属性无法完整表示该 Unicode 码点
    */

    // Char -> Unicode Uses Char.code property.

    fun isSurrogatePair(high: Byte, low: Byte): Boolean {
        return Character.isHighSurrogate(
            high.toInt().toChar()
        ) && Character.isLowSurrogate(
            low.toInt().toChar()
        )
    }


    infix fun Byte.isSurrogatePairWith(low: Byte): Boolean {
        return isSurrogatePair(this, low)
    }

    /**
     * 获取字符串的 Unicode 码点 (不拆分代理对)
     *
     * Returns the Unicode code points of this string.
     * And it won't split surrogate pairs.
     * @return 码点数组
     */
    fun String.getUnicodeCodePoints(): IntArray {
        return this.codePoints().toArray()
    }

    /**
     * 获取字符串的 Unicode 码点 (拆分代理对)
     *
     * Returns the Unicode code points of this string.
     * And it will split surrogate pairs.
     * @return 码点字节数组
     */
    fun String.getCodePointsBytes(): ByteArray {
        return this.toCharArray().map { it.code.toByte() }.toByteArray()
    }

    /**
     * 解码 Unicode 码点 (非拆分代理对)
     */
    fun decode(codePoint: Int): Char {
        return codePoint.toChar()
    }

    /**
     * 解码 Unicode 码点 (拆分代理对)
     */
    fun decodeSingle(high: Byte, low: Byte): Char {
        return decode((high.toInt() shl 16) + low)
    }

    /**
     * 解码 Unicode 码点 (处理拆分代理对)
     */
    fun decode(bytes: ByteArray): String {
        val chars = mutableListOf<Char>()
        var i = 0

        while (i < bytes.size) {
            // 读取两个字节组成一个 UTF-16 代码单元
            if (i + 1 < bytes.size) {
                val code = ((bytes[i].toInt() and 0xFF) shl 8) or (bytes[i + 1].toInt() and 0xFF)
                val char = code.toChar()

                // 检查是否为高位代理
                if (Character.isHighSurrogate(char) && i + 3 < bytes.size) {
                    // 读取下一个 UTF-16 代码单元（低位代理）
                    val lowCode = ((bytes[i + 2].toInt() and 0xFF) shl 8) or (bytes[i + 3].toInt() and 0xFF)
                    val lowChar = lowCode.toChar()

                    // 检查是否为有效的低位代理
                    if (Character.isLowSurrogate(lowChar)) {
                        // 组合成完整的码点并转换为字符
                        val codePoint = Character.toCodePoint(char, lowChar)
                        val decodedChars = Character.toChars(codePoint)
                        chars.addAll(decodedChars.toList())
                        i += 4 // 跳过已处理的4个字节
                        continue
                    }
                }

                chars.add(char)
                i += 2 // 跳过已处理的2个字节
            } else {
                // 处理剩余的单个字节（异常情况）
                i++
            }
        }

        return String(chars.toCharArray())
    }

    inline fun String.forEachCodePoint(action: (Int) -> Unit) {
        var i = 0
        while (i < this.length) {
            val codePoint = this.codePointAt(i)
            action(codePoint)
            i += Character.charCount(codePoint)
        }
    }

    fun isSurrogatePair(codePoint: Int): Boolean {
        return Character.isSurrogate(codePoint.toChar())
    }
}
