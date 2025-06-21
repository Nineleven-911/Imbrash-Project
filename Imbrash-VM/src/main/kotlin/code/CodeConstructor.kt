package hairinne.ip.vm.code

import hairinne.ip.vm.code.Bytecode.labelRetransfer

class CodeConstructor {
    private val codes: MutableList<Byte> = mutableListOf()
    private var returned = false

    private fun transfer(value: Long): List<Byte> {
        val byteCount = 8
        return (byteCount - 1 downTo 0).map { i ->
            ((value shr (i * 8)) and 0xFF).toByte()
        }.dropWhile { it == 0.toByte() }
    }
    private fun transfer(value: Int): List<Byte> {
        val byteCount = 4
        return (byteCount - 1 downTo 0).map { i ->
            ((value shr (i * 8)) and 0xFF).toByte()
        }.dropWhile { it == 0.toByte() }
    }
    private fun transfer(value: Short): List<Byte> {
        val byteCount = 2
        return (byteCount - 1 downTo 0).map { i ->
            ((value.toInt() shr (i * 8)) and 0xFF).toByte()
        }.dropWhile { it == 0.toByte() }
    }
    private fun bits(value: Int): Int {
        return when (value) {
            0 -> 0
            else -> 32 - value.countLeadingZeroBits()
        }
    }

    fun add(vararg code: Number): CodeConstructor {
        for (i in code) {
            when (i) {
                is Byte -> codes.add(i)
                is Short -> {
                    val tmp = transfer(i)
                    codes.addAll(tmp.ifEmpty { listOf<Byte>(0) })
                }
                is Int -> {
                    val tmp = transfer(i)
                    codes.addAll(tmp.ifEmpty { listOf<Byte>(0) })
                }
                is Long -> {
                    val tmp = transfer(i)
                    codes.addAll(tmp.ifEmpty { listOf<Byte>(0) })
                }
            }
        }
        return this
    }

    fun prints(characters: String): CodeConstructor {
        for (char in characters) {
            val code: Int = char.code
            val byteCount: Int = (bits(code) + 7) / 8
            this.add(
                Bytecode.PUSH, labelRetransfer(byteCount), code,
                Bytecode.PRT_C, byteCount,
                Bytecode.POP, labelRetransfer(byteCount)
            )
        }
        return this
    }

    fun ret(label: Int = 0): CodeConstructor {
        add(
            Bytecode.RET,
        )
        if (label != 0) {
            add(label.toByte())
        }
        returned = true
        return this
    }

    fun build(): MutableList<Byte> {
        if (!returned) {
            throw IllegalArgumentException("Code is not returned.")
        }
        return codes
    }
}