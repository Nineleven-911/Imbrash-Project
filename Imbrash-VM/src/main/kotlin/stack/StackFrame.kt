package hairinne.ip.vm.stack

import hairinne.ip.vm.code.EmptyOperandStackException
import hairinne.ip.vm.code.FunctionMeta
import hairinne.ip.vm.code.IterableOutOfBoundsException
import hairinne.ip.vm.code.NoSuchVariableException
import hairinne.ip.vm.vm.VMProperties.operandStackMaxSize

class StackFrame(
    var pc: Int = 0,
    val function: FunctionMeta
) {
    private var operandStack = ByteArray(128)
    private var localVariables: LongArray = LongArray(function.variableLength)
    private var stackPtr = 0
    var line = 0

    private fun expandable() {
        if (operandStack.size + 64 >= operandStackMaxSize) {
            throw IterableOutOfBoundsException(null, "Come on! Your stack storages $operandStackMaxSize Integers!")
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
            throw EmptyOperandStackException(null, "Stack pointer out of range.")
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
     * @return [ByteArray]
     */
    fun getValues(size: Int): ByteArray {
        return operandStack.slice((size() - size) until stackPtr).toByteArray()
    }

    fun getValues(size: Int, from: Int = size()): ByteArray {
        return operandStack.slice((from - size) until from).toByteArray()
    }


    /**
     * Copy stack values from a to b
     * @param to StackFrame(To)
     * @param size Count of bytes
     */
    fun copyStackValues(to: StackFrame, size: Int) {
        to.push(getValues(size))
    }

    fun setValue(index: Int, address: Long) {
        localVariables[index] = address
    }

    fun getValue(index: Int): Long {
        if (index >= localVariables.size) {
            throw NoSuchVariableException(
                null,
                "There's no such local variable in this scope."
            )
        }
        return localVariables[index]

    }

    override fun toString(): String {
        return "StackFrame(operandStack=${
            operandStack.slice(0 until stackPtr)
        }, stackPtr=$stackPtr, pc=$pc, localVars=${localVariables.toList()}, FunctionMeta=($function))"
    }
}