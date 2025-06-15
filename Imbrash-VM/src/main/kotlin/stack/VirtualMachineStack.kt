package hairinne.ip.vm.stack

class VirtualMachineStack {
    var stack: ByteArray = ByteArray(1024)
    var ptr: Int = 0

    fun expandable() {
        if (ptr + 10 >= stack.size) {
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
        expandable()
    }

    fun pop(): Byte {
        if (ptr > 0) {
            val value = stack[ptr - 1]
            shrinkable()
            return value
        }
        return 0
    }
}