package hairinne.ip.vm.code

data class Module(
    val code: MutableList<Byte>
) {
    override fun toString(): String {
        var result = ""
        for (i in code.indices) {
            result += "0x${code[i].toString(16)}"
            if (i != code.size - 1) {
                result += ", "
            }
        }
        return "[$result]"
    }
}
