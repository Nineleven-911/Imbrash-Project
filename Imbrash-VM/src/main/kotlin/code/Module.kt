package hairinne.ip.vm.code

import java.io.File

data class Module(
    val code: MutableList<Byte> = mutableListOf(),
    val functions: List<FunctionMeta> = emptyList(),
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
    // Code Section
    fun disassembledCS(file: String, append: Boolean = false) {
        var ptr = 0
        var line = 1
        val asm = AssemblyBuilder()
        // Each line: "Column:ByteCount | ByteCodes"
        while (ptr < code.size) {
            when (code[ptr++]) {
                Bytecode.PUSH -> {
                    val p = ptr - 1
                    val label = code[ptr++]
                    val byteCount = Bytecode.labelTransfer(label)
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
                    asm.append(line++, ptr - 2, "POP $label")
                }
                Bytecode.PRT -> {
                    val label = code[ptr++]
                    asm.append(line++, ptr - 2, "PRT $label")
                }
                Bytecode.RET -> {
                    val returns = code[ptr++]
                    asm.append(line++, ptr - 2, "RET $returns")
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
                    asm.append(line++, ptr - 1, "PRT_C")
                }
                Bytecode.BINARY_OP -> {
                    val op = code[ptr++]
                    asm.append(line++, ptr - 2, "BINARY_OP ${
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
                Bytecode.GOTO -> {
                    val p = ptr - 1
                    var number = 0u
                    for (i in 0 until 4) {
                        number = (number shl 8) + (code[ptr++].toInt() and 0xFF).toUInt()
                    }
                    asm.append(line++, p, "GOTO $number")
                }
                Bytecode.IF -> {
                    val p = ptr - 1
                    val cond = code[ptr++]
                    val offset =
                        ((code[ptr++].toInt() shl 24) and 0xFF000000.toInt()) +
                                ((code[ptr++].toInt() shl 16) and 0x00FF0000) +
                                ((code[ptr++].toInt() shl 8) and 0x0000FF00) +
                                (code[ptr++].toInt() and 0x000000FF)
                    when (cond) {
                        If.EQ -> asm.append(line++, p, "IF EQ, $offset")
                        If.NE -> asm.append(line++, p, "IF NE, $offset")
                        If.LT -> asm.append(line++, p, "IF LT, $offset")
                        If.GE -> asm.append(line++, p, "IF GE, $offset")
                        If.GT -> asm.append(line++, p, "IF GT, $offset")
                        If.LE -> asm.append(line++, p, "IF LE, $offset")
                        If.Integer.CMP_EQ -> asm.append(line++, p, "IF I_CMP_EQ, $offset")
                        If.Integer.CMP_NE -> asm.append(line++, p, "IF I_CMP_NE, $offset")
                        If.Integer.CMP_LT -> asm.append(line++, p, "IF I_CMP_LT, $offset")
                        If.Integer.CMP_GE -> asm.append(line++, p, "IF I_CMP_GE, $offset")
                        If.Integer.CMP_GT -> asm.append(line++, p, "IF I_CMP_GT, $offset")
                        If.Integer.CMP_LE -> asm.append(line++, p, "IF I_CMP_LE, $offset")
                        else -> {
                            asm.append(line++, p, "IF UNKNOWN, $offset")
                        }
                    }
                }

                else -> {
                    asm.append(line++, ptr - 1, "UNKNOWN")
                }
            }
        }
        if (append) {
            File(file).appendText(asm.toString() + "\n\n")
        } else {
            File(file).writeText(asm.toString() + "\n\n")
        }
    }

    // FunctionMeta Table
    fun disassembledFT(file: String, append: Boolean = false) {
        val table: MutableList<String> = mutableListOf()
        for (function in functions) {
            val name = function.name
            val id = function.id
            val start = function.start
            val end = function.end
            table.add("$id: $name, range: [$start, $end), LocalVariables require: ${function.variableLength}")
        }
        val t = """FunctionMeta Table: ${table.size}
            |${table.joinToString("\n")}
        """.trimMargin() + "\n\n"
        if (append) {
            File(file).appendText(t)
        } else {
            File(file).writeText(t)
        }
    }

    // Temporary put here
}
