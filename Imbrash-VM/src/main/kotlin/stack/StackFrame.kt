package hairinne.ip.vm.stack

import hairinne.ip.vm.code.EmptyOperandStackException
import hairinne.ip.vm.code.IterableOutOfBoundsException
import hairinne.ip.vm.vm.VMProperties.operandStackMaxSize

class StackFrame(var pc: Int = 0) {
    var operandStack = ByteArray(128)
    var stackPtr = 0
    var localVariables: MutableMap<Long, Long> = mutableMapOf()

    private fun expandable() {
        if (operandStack.size + 64 >= operandStackMaxSize) {
            throw IterableOutOfBoundsException(null, "Come on! Your stack storages ${operandStackMaxSize shr 10} Integers!")
        }
        if (stackPtr + 64 >= operandStack.size) {
            val tmp = ByteArray(operandStack.size + 64)
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

    fun push(values: ByteArray) {
        // require(values.size in listOf(1, 2, 4, 8))
        for (byte in values) {
            operandStack[stackPtr++] = byte
        }
        expandable()
    }

    fun pop(size: Int): ByteArray {
        require(size in listOf(1, 2, 4, 8))
        if (stackPtr-size < 0) {
            throw EmptyOperandStackException(null,"Stack pointer out of range.")
        }
        val ret = operandStack.slice(stackPtr - size until stackPtr).toByteArray()
        stackPtr -= size
        shrinkable()
        return ret
    }

    fun size(): Int {
        return stackPtr
    }

    /**
     * Get stack values
     * @param size Count of bytes
     * @return ByteArray
     */
    fun getValues(size: Int): ByteArray {
        return operandStack.slice(stackPtr - size until stackPtr).toByteArray()
    }


    /**
     * Copy stack values from a to b
     * @param to StackFrame(To)
     * @param size Count of bytes
     */
    fun copyStackValues(to: StackFrame, size: Int) {
        to.push(getValues(size))
    }

    override fun toString(): String {
        return "StackFrame(os=${
            operandStack.slice(0 until stackPtr)
        }, sptr=$stackPtr, pc=$pc, lv=$localVariables)"
    }
}