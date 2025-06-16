package hairinne.ip.vm.code

data class Module(
    val code: MutableList<Byte>
) {
    fun find(functions: Array<Function>, id: Long): IntRange? {
        val res = functions.find { it.id.id == id }
        return res?.location?.toRange()
    }
}
