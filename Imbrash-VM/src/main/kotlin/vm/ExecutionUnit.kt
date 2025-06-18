package hairinne.ip.vm.vm

import hairinne.ip.vm.code.Bytecode
import hairinne.ip.vm.code.EntrypointNotFoundException
import hairinne.ip.vm.code.Function
import hairinne.ip.vm.code.Module
import hairinne.ip.vm.stack.StackFrame
import hairinne.utils.ByteAndLong.LittleEndian.toLong
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
    val functions: Array<Function> /* Performance Sensitive uses Array */
) {
    val stack: Stack<StackFrame> = Stack()

    /**
     * Find function's entrypoint
     * @param id Function ID
     * @return IntRange of function's body with [start, end)
     */
    fun findFunction(id: Long): IntRange {
        val range = functions.find { it.id.id == id }?.location?.toRange()
        return range ?:
        throw EntrypointNotFoundException(this, "Function's entrypoint not found")
    }

    fun execute() {
        stack.push(StackFrame())
        var executing: StackFrame = stack.peek()
        // executing.pc = findFunction(0).start // While compiling, the compiler will add an entrypoint function, now it's not written.
        val code = module.code

        while (true) {
            when (code[executing.pc++]) {
                Bytecode.PUSH -> {
                    val label = code[executing.pc++]
                    require(label in 0 until 4)
                    for (i in 0 until Bytecode.labelTransfer(label)) {
                        executing.push(code[executing.pc++])
                    }
                }
                Bytecode.POP -> {
                    val label: Byte = code[executing.pc++]
                    require(label in 0 until 4)
                    for (i in 0 until Bytecode.labelTransfer(label)) {
                        executing.pop()
                    }
                }
                Bytecode.PRT -> {
                    val label: Byte = code[executing.pc++]
                    require(label in 0 until 4)
                    print(stack.peek().getStackValues(
                        Bytecode.labelTransfer(label)
                    ).toLong())
                }
                Bytecode.RET -> {
                    val label: Byte = code[executing.pc++]
                    require(label in 0 until 5)

                    if (label != 0.toByte()) {
                        val size: Int = Bytecode.labelTransfer(label)
                        stack.peek().copyStackValues(
                            stack[stack.size - 2],
                            size)
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
                    stack.push(StackFrame(findFunction(id.toLong()).start))

                    if (label != 0.toByte()) {
                        val size: Int = Bytecode.labelTransfer(label)
                        executing.copyStackValues(stack.peek(), size)
                    }
                    executing = stack.peek()
                }
                Bytecode.PRT_C -> {
                    TODO("Trying to parse a char in bytearray")
                } // Print as Unicode (A single char)
            }
        }
    }
}

