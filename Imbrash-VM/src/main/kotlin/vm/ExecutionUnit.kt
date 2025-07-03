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
        val range = functions.find { it.id.id == id }?.location?.toRange()
        return range ?: throw EntrypointNotFoundException(this, "Function's entrypoint not found")
    }

    fun execute() {
        stack.push(StackFrame())
        var executing: StackFrame = stack.peek()
        // While compiling, the compiler will add an entrypoint function, now it's not written.
        // executing.pc = findFunction(0).start
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
                            "Stack is empty. Pop `null` instead!"
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
                    if (stack.isEmpty() or (label == 0.toByte())) {
                        return
                    } else {
                        executing = stack.peek()
                    }
                }
                Bytecode.CALL -> {
                    val id: Byte = code[executing.pc++]
                    val label: Byte = code[executing.pc++]
                    require(label in 0 until 5)
                    val frame = StackFrame(findFunction(id.toLong()).start)
                    stack.push(frame)

                    if (label != 0.toByte()) {
                        val size: Int = Bytecode.labelTransfer(label)
                        executing.copyStackValues(frame, size)
                    }
                    executing = frame
                }
                Bytecode.PRT_C -> {
                    val label = code[executing.pc++]
                    require(label in 1..4)
                    print(
                        Unicodes.decode(
                            executing.pop(
                                label.toInt()
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
                            0 -> tmp1 + tmp2
                            1 -> tmp1 - tmp2
                            2 -> tmp1 * tmp2
                            3 -> tmp1 / tmp2
                            4 -> tmp1 % tmp2
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
                            0 -> tmp1 + tmp2
                            1 -> tmp1 - tmp2
                            2 -> tmp1 * tmp2
                            3 -> tmp1 / tmp2
                            4 -> (tmp1.toDouble() % tmp2.toDouble()).toFloat()
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
                            0 -> tmp1 + tmp2
                            1 -> tmp1 - tmp2
                            2 -> tmp1 * tmp2
                            3 -> tmp1 / tmp2
                            4 -> (tmp1 % tmp2)
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
}

