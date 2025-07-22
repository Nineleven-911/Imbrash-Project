package hairinne.ip.vm.code

data class Module(
    val code: MutableList<Byte> = mutableListOf()
) {
    override fun toString(): String {
        var result = ""
        for (i in code.indices) {
            result += "0x${code[i].toUByte().toString(16)}"
            if (i != code.size - 1) {
                result += ", "
            }
        }
        return "[$result]"
    }

    fun fromRaw(raw: ByteArray) {
        code.clear()
        TODO()
    }

    fun toRaw(): ByteArray {
        TODO()
    }
}
