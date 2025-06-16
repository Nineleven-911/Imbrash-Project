package hairinne.ip.vm.vm

import hairinne.ip.vm.code.Bytecode
import hairinne.ip.vm.code.EntrypointNotFoundException
import hairinne.ip.vm.code.Function
import hairinne.ip.vm.code.Module
import hairinne.ip.vm.stack.StackFrame
import java.util.Stack

class ExecutionUnit(
    val module: Module,
    val functions: Array<Function>
) {
    /**
     * Execution Unit for Imbrash Virtual Machine (the IVM)
     *
     * Stack-based, Little endian
     *
     * @author Hairinne
     */
    val stack: Stack<StackFrame> = Stack()

    fun execute(): ByteArray {
        val code = module.code.slice({
            val res = functions.find { it.id.id == 0L }
            res?.location?.toRange() ?: throw EntrypointNotFoundException(this, "Entrypoint not found.")
        }.invoke())
        var executing: StackFrame = stack.peek()

        while (true) {
            when (code[executing.pc++]) {
                Bytecode.PUSH -> {
                }
                Bytecode.POP -> {
                }
                Bytecode.PRT -> {
                }
                Bytecode.RET -> {
                    val label: Byte = code[executing.pc++]

                    if (label != 0.toByte()) {
                        for (i in (0 until Bytecode.labelTransfer(label - 1)).reversed()) {
                            stack.peek().operandStack.push(
                                executing.operandStack.pop()
                            )
                        }
                    }
                    stack.pop()
                    executing = stack.peek()
                }
                Bytecode.CALL -> {
                    val label: Byte = code[executing.pc++]
                    TODO()
                }
            }
        }
    }
}

