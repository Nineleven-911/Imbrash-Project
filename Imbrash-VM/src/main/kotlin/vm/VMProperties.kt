package hairinne.ip.vm.vm

data object VMProperties {
    var operandStackMaxSize: Int = 0
    var recursiveLimit: Int = 0

    fun set(operandStackMaxSize: Int, recursiveLimit: Int) {
        this.operandStackMaxSize = operandStackMaxSize
        this.recursiveLimit = recursiveLimit
    }

    override fun toString(): String {
        return "IVMProperties(operandStackMaxSize=$operandStackMaxSize, recursiveLimit=$recursiveLimit)"
    }
}
