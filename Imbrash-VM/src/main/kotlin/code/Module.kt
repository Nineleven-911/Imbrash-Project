package hairinne.ip.vm.code

import java.io.File

data class Module(
    val code: MutableList<Byte> = mutableListOf()
) {
    // Temporary put here
    private class AssemblyBuilder {
        private data class Command(
            var column: Int = 0,
            var byteCount: Int = 0,
            var value: String = ""
            )
        private val commands: MutableList<Command> = mutableListOf()
        fun append(col: Int, byteCount: Int, value: String) {
            commands.add(Command(col, byteCount, value))
        }

        override fun toString(): String {
            var res = ""
            val colPad = commands.map { it.column }.maxOfOrNull { it.toString().length }!!
            val byteCountPad = commands.map { it.byteCount }.maxOfOrNull { it.toString().length }!!
            for (command in commands) {
                val col = command.column.toString().padStart(colPad)
                val byteCount = command.byteCount.toString().padStart(byteCountPad)
                res += "$col:$byteCount | ${command.value}\n"
            }
            return res
        }
    }
    override fun toString(): String {
        var result = ""
        for (i in code.indices) {
            result += "0x${code[i].toUByte().toString(16).padStart(2, '0')}"
            if (i != code.size - 1) {
                result += ", "
            }
        }
        return "[$result]"
    }

    fun fromRaw(raw: ByteArray) {
        code.clear()
        TODO()
    }

    fun toRaw(): ByteArray {
        TODO()
    }

    // Temporary put here
    fun disassembledCS(file: String) {
        var ptr = 0
        var line = 1
        val asm = AssemblyBuilder()
        // Each line: "Column:ByteCount | ByteCodes"
        while (ptr < code.size) {
            when (code[ptr++]) {
                Bytecode.PUSH -> {
                    val label = code[ptr++]
                    val byteCount = Bytecode.labelTransfer(label)
                    val p = ptr - 1
                    var number = 0UL
                    var builder = "PUSH $label "
                    for (i in 0 until byteCount) {
                        val byte = code[ptr++]
                        number = (number shl 8) + byte.toUByte()
                        builder += "0x${byte.toUByte().toString(16).uppercase()}"
                        builder += if (i != byteCount - 1) ", " else ""
                    }
                    asm.append(line++, p, "$builder  // Number: $number")
                }
                Bytecode.POP -> {
                    val label = code[ptr++]
                    asm.append(line++, ptr - 1, "POP $label")
                }
                Bytecode.PRT -> {
                    val label = code[ptr++]
                    asm.append(line++, ptr - 1, "PRT $label")
                }
                Bytecode.RET -> {
                    val returns = code[ptr++]
                    asm.append(line++, ptr - 1, "RET $returns")
                }
                Bytecode.CALL -> {
                    val p = ptr - 1
                    var id = 0L
                    for (i in 0 until 8) {
                        id = (id shl 8) + code[ptr++]
                    }
                    val bitCount = code[ptr++]
                    asm.append(line++, p, "CALL $id, $bitCount")
                }
                Bytecode.PRT_C -> {
                    asm.append(line++, ptr - 1, "PRT_C ")
                }
                Bytecode.BINARY_OP -> {
                    val op = code[ptr++]
                    asm.append(line++, ptr - 1, "BINARY_OP ${
                        when (op) {
                            BinaryOperator.ADD -> "ADD"
                            BinaryOperator.SUB -> "SUB"
                            BinaryOperator.MUL -> "MUL"
                            BinaryOperator.DIV -> "DIV"
                            BinaryOperator.MOD -> "MOD"
                            else -> "UNKNOWN"
                        }
                    }")
                }
                else -> {
                    asm.append(line++, ptr - 1, "UNKNOWN")
                }
            }
        }
        File(file).writeText(asm.toString())
    }
}
