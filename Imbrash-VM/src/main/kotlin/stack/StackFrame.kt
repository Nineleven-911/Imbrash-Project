package hairinne.ip.vm.stack

import hairinne.utils.Overlooks

class StackFrame(var pc: Int = 0) {
    private var operandStack = ByteArray(128)
    private var stackPtr = 0
    private var localVariables: MutableMap<Long, Long> = mutableMapOf()

    private fun expandable() {
        if (stackPtr + 64 >= operandStack.size) {
            val tmp = ByteArray(operandStack.size shl 1)
            System.arraycopy(
                operandStack,
                0,
                tmp,
                0,
                operandStack.size
            )
            operandStack = tmp
        }
    }

    private fun shrinkable() {
        if (stackPtr < operandStack.size shr 2) {
            val tmp = ByteArray(operandStack.size shr 1)
            for (i in tmp.indices) {
                tmp[i] = operandStack[i]
            }
            operandStack = tmp
        }
    }

    fun push(value: Byte) {
        operandStack[stackPtr++] = value
        expandable()
    }

    fun pop(): Byte? {
        if (stackPtr == 0) {
            return null
        }
        val res = operandStack[--stackPtr]
        shrinkable()
        return res
    }

    fun getStack(): ByteArray {
        return operandStack
    }

    /**
     * Get stack values
     * @param size Count of bytes
     * @return ByteArray
     */
    fun getStackValues(size: Int): ByteArray {
        val arguments = ByteArray(size)
        for (i in (0 until size).reversed()) {
            arguments[i] = operandStack[stackPtr - i - 1]
        }
        return arguments
    }

    /**
     * Copy stack values from a to b
     * @param to StackFrame(To)
     * @param size Count of bytes
     */
    fun copyStackValues(to: StackFrame, size: Int) {
        for (byte in this.getStackValues(size)) {
            to.push(byte)
        }
    }

    override fun toString(): String {
        return """SF( os=${Overlooks.list(operandStack.toList())}}, sptr=$stackPtr, pc=$pc, lv=$localVariables )"""
    }
}