package hairinne.ip.vm.stack

class VirtualMachineStack {
    var stack: ByteArray = ByteArray(128)
    var ptr: Int = 0

    fun expandable() {
        if (ptr + 64 >= stack.size) {
            val tmp = ByteArray(stack.size shl 1)
            System.arraycopy(stack, 0, tmp, 0, stack.size)
            stack = tmp
        }
    }

    fun shrinkable() {
        if (ptr < stack.size shr 2) {
            val tmp = ByteArray(stack.size shr 1)
            System.arraycopy(stack, 0, tmp, 0, tmp.size)
            stack = tmp
        }
    }

    fun push(value: Byte) { // 只压入一个字节
        stack[ptr++] = value
        // expandable()
    }

    fun pop(): Byte {
        if (ptr > 0) {
            val value = stack[ptr--]
            shrinkable()
            return value
        }
        return 0
    }

    fun get(index: Int = ptr): Byte {
        return stack[index - 1]
    }

    /**
     * Get stack values
     * @param a VirtualMachineStack
     * @param size Count of bytes
     * @return ByteArray
     */
    fun getStackValues(size: Int): ByteArray {
        val arguments = ByteArray(size)
        for (i in (0 until size).reversed()) {
            arguments[i] = this.get(
                this.ptr - i
            )
        }
        return arguments
    }

    /**
     * Copy stack values from a to b
     * @param a VirtualMachineStack (From)
     * @param b VirtualMachineStack (To)
     * @param size Count of bytes
     */
    fun copyStackValues(to: VirtualMachineStack, size: Int) {
        for (byte in this.getStackValues(size)) {
            to.push(byte)
        }
    }
}