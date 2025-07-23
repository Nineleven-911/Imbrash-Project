package hairinne.ip.vm.vm

import hairinne.ip.vm.code.*
import hairinne.ip.vm.code.Function
import hairinne.ip.vm.stack.StackFrame
import hairinne.utils.ByteAndLong.toByteArray
import hairinne.utils.ByteAndLong.toLong
import hairinne.utils.Unicodes
import java.util.*

/**
 * Execution Unit for Imbrash Virtual Machine (the IVM)
 *
 * Stack-based, Little endian
 *
 * @author Hairinne
 */

class ExecutionUnit(
    val module: Module,
    val functions: Array<Function> // Use Array for better performance
) {
    val stack: Stack<StackFrame> = Stack()

    /**
     * Find function's entrypoint
     * @param id Function ID
     * @return IntRange of function's body with [start, end)
     */
    fun findFunction(id: Long): IntRange {
        return getFunction(functions, id).getRange()
    }

    fun execute() {
        stack.push(StackFrame())
        var executing: StackFrame = stack.peek()
        // While compiling, the compiler will add an entrypoint function
        val function = findFunction(0)
        executing.pc = function.first
        val code = module.code

        while (true) {
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
                        executing.pop(
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
                            stack[stack.size - 2],
                            size
                        )
                    }
                    stack.pop()
                    executing = stack.peek()
                }
                Bytecode.CALL -> {
                    require(stack.size < VMProperties.recursiveLimit) {
                        "Stack Overflow (${stack.size + 1} > ${VMProperties.recursiveLimit}). You should open your browser if you want to join 'StackOverflow'."
                    }
                    var id = 0L
                    for (i in 0 until 8) {
                        id = (id shl (i*8)) + code[executing.pc++].toLong()
                    }
                    val bytesToPut: UByte = code[executing.pc++].toUByte()
                    val frame = StackFrame(findFunction(id).first)
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

                Debugs.DEBUG -> executing.pc = Debugs.debug(this)
                else -> throw BytecodeNotFoundException(
                    this,
                    "Invalid bytecode. ${code[executing.pc - 1]}"
                )
            }
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
     * Operator [] will return the byte of cs of eu.
     */
    operator fun get(index: Int): Byte {
        return module.code[index]
    }
}

