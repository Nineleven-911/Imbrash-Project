package hairinne.ip.vm.code

import hairinne.utils.Unicodes
import hairinne.utils.Unicodes.forEachCodePoint

class CodeConstructor {
    private val codes: MutableList<Byte> = mutableListOf()
    private val functions: MutableList<Function> = mutableListOf()
    private var returned = false

    private var isFirstBuildingCurrentFunction: Boolean = true
    private var currentFunctionId: Long = 0
    private var currentFunctionStart: Int = 0

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

    fun ret(label: Int = 0): CodeConstructor {
        add(
            Bytecode.RET, label.toByte()
        )
        returned = true
        return this
    }

    fun build(): MutableList<Byte> {
        if (!returned) {
            throw IllegalArgumentException("Code is not returned.")
        }
        functions.add(
            Function(currentFunctionId, currentFunctionStart, codes.size)
        )
        return codes
    }

    fun printf(string: String): CodeConstructor {
        string.forEachCodePoint {
            add(Bytecode.PUSH, 2)
            if (Unicodes.isSurrogatePair(it)) {
                add(
                    ((it and 0xFF000000.toInt()) shr 8 * 3).toByte(),
                    ((it and 0xFF0000) shr 8 * 2).toByte(),
                    ((it and 0xFF00) shr 8).toByte(),
                    (it and 0xFF).toByte()
                )
            } else {
                add(
                    0, 0,
                    ((it and 0xFF00) shr 8).toByte(),
                    (it and 0xFF).toByte()
                )
            }
            add(Bytecode.PRT_C)
            add(Bytecode.POP, 2)
        }
        return this
    }

    fun fibonacci(): CodeConstructor {
        TODO()
    }

    fun getFunctions(): List<Function> {
        return functions
    }

    fun function(id: Long): CodeConstructor {
        if (isFirstBuildingCurrentFunction) {
            isFirstBuildingCurrentFunction = false
        } else {
            functions.add(
                Function(currentFunctionId, currentFunctionStart, codes.size)
            )
        }
        currentFunctionId = id
        currentFunctionStart = codes.size
        return this
    }
}