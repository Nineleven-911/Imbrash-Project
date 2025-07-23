package hairinne.utils

object ByteAndLong {
    /**
     * Translate List of Byte to Long.
     *
     * Example:
     * ```[0x4F, 0x60] -> 0x4F60```
     * ```[79, 96] -> 20320```
     *
     * @return result in Long.
     */
    fun List<Byte>.toLong(): Long {
        var result = 0L
        for (i in this) {
            result = (result shl 8) + (i.toLong() and 0xFF)
        }
        return result
    }

    /**
     * Translate ByteArray to Long.
     * returns ```List<Byte>.toLong()```
     */
    fun ByteArray.toLong(): Long {
        return this.toList().toLong()
    }

    /**
     * Translate Long to ByteArray.
     *
     * Example:
     * ```0x4F60 -> [0x4F, 0x60]```
     * ```20320 -> [79, 96]```
     *
     * @return result in ByteArray.
     */
    fun Long.toByteArray(): List<Byte> {
        val result = mutableListOf<Byte>()
        for (i in 0 until 8) {
            result.add(
                ((this shr (i * 8)) and 0xFF.toLong()).toByte()
            )
        }
        return result.toList().reversed().dropWhile { it == 0.toByte() }
    }
}
