package hairinne.ip.vm.stack

class StackFrame(var pc: Int = 0) {
    var operandStack = ByteArray(128)
    var stackPtr = 0
    var localVariables: MutableMap<Long, Long> = mutableMapOf()

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

    fun size(): Int {
        return stackPtr
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
        return operandStack.slice(stackPtr - size until stackPtr).toByteArray()
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
        return "StackFrame(os=${operandStack.toList().dropLastWhile { it == 0.toByte() && this.size() > 1 }}, sptr=$stackPtr, pc=$pc, lv=$localVariables)"
    }
}