package hairinne.ip.vm.vm

import hairinne.ip.vm.code.*
import hairinne.ip.vm.code.Function
import hairinne.ip.vm.stack.StackFrame
import hairinne.utils.ByteAndLong.toByteArray
import hairinne.utils.ByteAndLong.toLong
import hairinne.utils.Unicodes
import java.io.File
import java.util.*

/**
 * Execution Unit for Imbrash Virtual Machine (the IVM)
 *
 * Stack-based, Little endian
 *
 * @author Hairinne-Mentine.
 */

class ExecutionUnit(
    val module: Module
) {
    val stack: Stack<StackFrame> = Stack()
    val functions = module.functions.toTypedArray() // Use Array for better performance

    /**
     * Find function's entrypoint
     * @param id Function ID
     * @return A `hairinne.ip.vm.code.Function` instance.
     */
    fun findFunction(id: Long): Function {
        return getFunction(functions, id)
    }

    fun execute() {
        // While compiling, the compiler will add an entrypoint function
        var function = findFunction(0)
        stack.push(StackFrame(function = function))
        var executing: StackFrame = stack.peek()
        executing.pc = function.start
        val code = module.code
        val file = File("A.txt")
        file.writeText("")

        while (true) {
            val time = System.nanoTime()
            when (code[executing.pc++]) {
                Bytecode.PUSH -> {
                    val label = code[executing.pc++]
                    require(label in 0 until 4)
                    val size = Bytecode.labelTransfer(label)
                    val value = code.slice(executing.pc until executing.pc + size).toByteArray()
                    executing.push(value)
                    executing.pc += size
                }
                Bytecode.POP -> {
                    val byteCount = Bytecode.labelTransfer(code[executing.pc++])
                    require(byteCount in listOf(1, 2, 4, 8))
                    if (byteCount > executing.size()) {
                        throw EmptyOperandStackException(
                            this,
                            "Stack is empty. Should stack pop `null` instead?"
                        )
                    }
                    executing.pop(byteCount)
                }
                Bytecode.PRT -> {
                    val label: Byte = code[executing.pc++]
                    require(label in 0 until 4)
                    print(
                        executing.getValues(
                            Bytecode.labelTransfer(label)
                        ).toLong()
                    )
                }
                Bytecode.RET -> {
                    if (stack.size == 1) {
                        stack.pop()
                        return
                    }
                    val label: Byte = code[executing.pc++]
                    require(label in 0 until 5)

                    if (label != 0.toByte()) {
                        val size: Int = Bytecode.labelTransfer(label)
                        executing.copyStackValues(
                            stack[stack.size - 2], size
                        )
                    }
                    stack.pop()
                    executing = stack.peek()
                }
                Bytecode.CALL -> {
                    require(stack.size < VMProperties.recursiveLimit) {
                        throw RecursionTooDeepException(
                            this,
                            "Stack Overflow (${stack.size + 1} > ${VMProperties.recursiveLimit}). You should open your browser if you want to join 'StackOverflow'."
                        )
                    }
                    val id =
                        ((code[executing.pc++].toInt() and 0xFF) shl 56).toLong() +
                                ((code[executing.pc++].toInt() and 0xFF) shl 48).toLong() +
                                ((code[executing.pc++].toInt() and 0xFF) shl 40).toLong() +
                                ((code[executing.pc++].toInt() and 0xFF) shl 32).toLong() +
                                ((code[executing.pc++].toInt() and 0xFF) shl 24).toLong() +
                                ((code[executing.pc++].toInt() and 0xFF) shl 16).toLong() +
                                ((code[executing.pc++].toInt() and 0xFF) shl 8).toLong() +
                                (code[executing.pc++].toInt() and 0xFF).toLong()
                    val bytesToPut: UByte = code[executing.pc++].toUByte()
                    function = findFunction(id)
                    val frame = StackFrame(function.start, function)
                    stack.push(frame)
                    executing.copyStackValues(frame, bytesToPut.toInt())
                    executing = frame
                }
                Bytecode.PRT_C -> {
                    print(
                        Unicodes.decode(
                            executing.getValues(
                                4
                            ).toLong().toInt()
                        )
                    )
                }
                Bytecode.BINARY_OP -> {
                    val label = code[executing.pc++].toInt()
                    val type = label and 0x0F
                    val size = Bytecode.labelTransfer(type)
                    val op1 = executing.pop(size)
                    val op2 = executing.pop(size)
                    if (size <= 3) {
                        val tmp1 = op1.toLong()
                        val tmp2 = op2.toLong()
                        val result = when ((label shr 4) and 0x0F) {
                            BinaryOperator.ADD.toInt() -> tmp1 + tmp2
                            BinaryOperator.SUB.toInt() -> tmp1 - tmp2
                            BinaryOperator.MUL.toInt() -> tmp1 * tmp2
                            BinaryOperator.DIV.toInt() -> tmp1 / tmp2
                            BinaryOperator.MOD.toInt() -> tmp1 % tmp2
                            else -> throw InvalidDataException(
                                this,
                                "Invalid type id."
                            )
                        }
                        val ret = ByteArray(size)
                        for (i in size - 1 downTo 0) ret[i] = ((result shr (i * 8)) and 0xFF).toByte()
                        if (label < 3) {
                            val sign = result and (1L shl 63)
                            if (sign != 0L) ret[0] = (0x80 or (ret[0].toInt() and 0x7F)).toByte()
                            else ret[0] = (ret[0].toInt() and 0x7F).toByte()
                        }
                        executing.push(ret)
                    } else if (size == 4) {
                        val tmp1 = Float.fromBits(op1.toLong().toInt())
                        val tmp2 = Float.fromBits(op2.toLong().toInt())
                        val ret = when ((label shr 4) and 0x0F) {
                            BinaryOperator.ADD.toInt() -> tmp1 + tmp2
                            BinaryOperator.SUB.toInt() -> tmp1 - tmp2
                            BinaryOperator.MUL.toInt() -> tmp1 * tmp2
                            BinaryOperator.DIV.toInt() -> tmp1 / tmp2
                            BinaryOperator.MOD.toInt() -> (tmp1.toDouble() % tmp2.toDouble()).toFloat()
                            else -> throw InvalidDataException(
                                this,
                                "Invalid type id."
                            )
                        }
                        executing.push(
                            ret.toBits().toLong().toByteArray().toByteArray()
                        )
                    } else if (size == 5) {
                        val tmp1 = Double.fromBits(op1.toLong())
                        val tmp2 = Double.fromBits(op2.toLong())
                        val ret = when ((label shr 4) and 0x0F) {
                            BinaryOperator.ADD.toInt() -> tmp1 + tmp2
                            BinaryOperator.SUB.toInt() -> tmp1 - tmp2
                            BinaryOperator.MUL.toInt() -> tmp1 * tmp2
                            BinaryOperator.DIV.toInt() -> tmp1 / tmp2
                            BinaryOperator.MOD.toInt() -> tmp1 % tmp2
                            else -> throw InvalidDataException(
                                this,
                                "Invalid data. "
                            )
                        }
                        executing.push(
                            ret.toBits().toByteArray().toByteArray()
                        )
                    }
                }
                Bytecode.GOTO -> {
                    val offset =
                        ((code[executing.pc++].toInt() shl 24) and 0xFF000000.toInt()) +
                                ((code[executing.pc++].toInt() shl 16) and 0x00FF0000) +
                                ((code[executing.pc++].toInt() shl 8) and 0x0000FF00) +
                                (code[executing.pc++].toInt() and 0x000000FF)
                    executing.pc = offset
                }
                Bytecode.IF -> {
                    val type = code[executing.pc++]
                    val num = executing.pop(4).toLong().toInt()
                    val offset =
                        ((code[executing.pc++].toInt() shl 24) and 0xFF000000.toInt()) +
                                ((code[executing.pc++].toInt() shl 16) and 0x00FF0000) +
                                ((code[executing.pc++].toInt() shl 8) and 0x0000FF00) +
                                (code[executing.pc++].toInt() and 0x000000FF)
                    when (type) {
                        If.EQ -> if (num == 0) executing.pc = offset
                        If.NE -> if (num != 0) executing.pc = offset
                        If.LT -> if (num < 0) executing.pc = offset
                        If.GE -> if (num >= 0) executing.pc = offset
                        If.GT -> if (num > 0) executing.pc = offset
                        If.LE -> if (num <= 0) executing.pc = offset
                        in If.Integer.CMP_EQ..If.Integer.CMP_LE -> {
                            val num2 = executing.pop(4).toLong().toInt()
                            when (type) {
                                If.Integer.CMP_EQ -> if (num == num2) executing.pc = offset
                                If.Integer.CMP_NE -> if (num != num2) executing.pc = offset
                                If.Integer.CMP_LT -> if (num < num2) executing.pc = offset
                                If.Integer.CMP_GE -> if (num >= num2) executing.pc = offset
                                If.Integer.CMP_GT -> if (num > num2) executing.pc = offset
                                If.Integer.CMP_LE -> if (num <= num2) executing.pc = offset
                                else -> throw InvalidDataException(
                                    this,
                                    "Invalid IfConditionalJump.Integer type: $type"
                                )
                            }
                        }
                        else -> {
                            throw InvalidDataException(
                                this,
                                "Invalid IfConditionalJump type: $type"
                            )
                        }
                    }
                }

                Debugs.DEBUG -> executing.pc = Debugs.debug(this)
                else -> throw BytecodeNotFoundException(
                    this,
                    "Invalid bytecode. ${code[executing.pc - 1]}"
                )
            }
            file.appendText("${System.nanoTime() - time} ns\n")
            executing.line++
        }
    }

    fun exec() {
        try {
            execute()
        } catch (e: Exception) {
            throw RuntimeException(this, "Execute failed. $e")
        }
    }


    /**
     * Operator [] will return this.module.code[ index ] (CodeSegment)
     */
    operator fun get(index: Int): Byte {
        return module.code[index]
    }

    fun getStackTrace(spaces: Int = 4): String {
        val trace: MutableList<String> = mutableListOf()
        for (frame in stack) {
            trace.add(" ".repeat(spaces) + "at <Module>(${frame.function.name}:${frame.line})")
        }
        // multi-thread is not implemented yet
        return "An exception occurred in Thread \"null\":\n${trace.joinToString("\n")}"
    }
}

