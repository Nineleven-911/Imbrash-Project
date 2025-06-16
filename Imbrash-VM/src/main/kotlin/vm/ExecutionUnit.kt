package hairinne.ip.vm.vm

import hairinne.ip.vm.code.Bytecode
import hairinne.ip.vm.code.EntrypointNotFoundException
import hairinne.ip.vm.code.Function
import hairinne.ip.vm.code.Module
import hairinne.ip.vm.stack.StackFrame

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

    /**
     * Get stack values
     * @param stack the stack frame
     * @param size Count of bytes
     * @return ByteArray
     */
    fun getStackValues(stackFrame: StackFrame, size: Int): ByteArray {
        val arguments = ByteArray(size)
        for (i in (0 until size).reversed()) {
            arguments[i] = stackFrame.operandStack.pop()
        }
        for (i in arguments) {
            stackFrame.operandStack.push(i)
        }
        return arguments
    }

    /**
     * Copy stack values from a to b
     * @param a StackFrame (From)
     * @param b StackFrame (To)
     * @param size Count of bytes
     */
    fun copyStackValues(a: StackFrame, b: StackFrame, size: Int) {
        val arguments = ByteArray(size)
        for (i in (0 until size).reversed()) {
            arguments[i] = a.operandStack.pop()
        }
        for (i in arguments) {
            a.operandStack.push(i)
            b.operandStack.push(i)
        }
    }

    fun execute() {
        var executing: StackFrame = stack.peek()
        executing.pc = findFunction(0).start
        val code = module.code

        while (true) {
            when (code[executing.pc++]) {
                Bytecode.PUSH -> {
                    val label = code[executing.pc++]
                    require(label in 0 until 4)
                    for (i in 0 until Bytecode.labelTransfer(label)) {
                        executing.operandStack.push(code[executing.pc++])
                    }
                }
                Bytecode.POP -> {
                    val label: Byte = code[executing.pc++]
                    require(label in 0 until 4)
                    for (i in 0 until Bytecode.labelTransfer(label)) {
                        executing.operandStack.pop()
                    }
                }
                Bytecode.PRT -> {
                    val label: Byte = code[executing.pc++]
                    require(label in 0 until 4)

                }
                Bytecode.RET -> {
                    val label: Byte = code[executing.pc++]
                    require(label in 0 until 5)

                    if (label != 0.toByte()) {
                        val size: Int = Bytecode.labelTransfer(label)
                        copyStackValues(executing, stack[stack.size - 2], size)
                    }
                    stack.pop()
                    executing = stack.peek()
                }
                Bytecode.CALL -> {
                    val id: Byte = code[executing.pc++]
                    val label: Byte = code[executing.pc++]
                    require(label in 0 until 5)
                    stack.push(StackFrame(findFunction(id.toLong()).start))

                    if (label != 0.toByte()) {
                        val size: Int = Bytecode.labelTransfer(label)
                        copyStackValues(executing, stack.peek(), size)
                    }
                    executing = stack.peek()
                }
                Bytecode.PRT_C -> {

                } // Print as Unicode & ascii (A single char)
            }
        }
    }
}

