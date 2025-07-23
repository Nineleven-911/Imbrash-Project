package hairinne.ip.vm.code

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

    fun helloWorld(): CodeConstructor {
        return CodeConstructor().add(
            Bytecode.PUSH, 2, 0, 0, 0, 'H'.code,
            Bytecode.PRT_C,
            Bytecode.PUSH, 2, 0, 0, 0, 'e'.code,
            Bytecode.PRT_C,
            Bytecode.PUSH, 2, 0, 0, 0, 'l'.code,
            Bytecode.PRT_C,
            Bytecode.PUSH, 2, 0, 0, 0, 'l'.code,
            Bytecode.PRT_C,
            Bytecode.PUSH, 2, 0, 0, 0, 'o'.code,
            Bytecode.PRT_C,
            Bytecode.PUSH, 2, 0, 0, 0, ','.code,
            Bytecode.PRT_C,
            Bytecode.PUSH, 2, 0, 0, 0, ' '.code,
            Bytecode.PRT_C,
            Bytecode.PUSH, 2, 0, 0, 0, 'W'.code,
            Bytecode.PRT_C,
            Bytecode.PUSH, 2, 0, 0, 0, 'o'.code,
            Bytecode.PRT_C,
            Bytecode.PUSH, 2, 0, 0, 0, 'r'.code,
            Bytecode.PRT_C,
            Bytecode.PUSH, 2, 0, 0, 0, 'l'.code,
            Bytecode.PRT_C,
            Bytecode.PUSH, 2, 0, 0, 0, 'd'.code,
            Bytecode.PRT_C,
            Bytecode.PUSH, 2, 0, 0, 0, '!'.code,
            Bytecode.PRT_C
        ).ret()
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