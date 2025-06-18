package hairinne.ip.vm.code

class CodeConstructor {
    private val codes: MutableList<Byte> = mutableListOf()

    fun transfer(value: Long): List<Byte> {
        val byteCount = 8
        return (byteCount - 1 downTo 0).map { i ->
            ((value shr (i * 8)) and 0xFF).toByte()
        }.dropWhile { it == 0.toByte() }
    }
    fun transfer(value: Int): List<Byte> {
        val byteCount = 4
        return (byteCount - 1 downTo 0).map { i ->
            ((value shr (i * 8)) and 0xFF).toByte()
        }.dropWhile { it == 0.toByte() }
    }
    fun transfer(value: Short): List<Byte> {
        val byteCount = 2
        return (byteCount - 1 downTo 0).map { i ->
            ((value.toInt() shr (i * 8)) and 0xFF).toByte()
        }.dropWhile { it == 0.toByte() }
    }

    fun add(vararg code: Number): CodeConstructor {
        for (i in code) {
            when (i) {
                is Byte -> codes.add(i)
                is Short -> codes.addAll(transfer(i))
                is Int -> codes.addAll(transfer(i))
                is Long -> codes.addAll(transfer(i))
            }
        }
        return this
    }

    fun build(): MutableList<Byte> {
        return codes
    }
}