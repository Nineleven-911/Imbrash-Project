package hairinne.ip.vm.vm

data object VMProperties {
    var operandStackMaxSize: Int = 4096
    var recursiveLimit: Int = 1024
    var callingStackOptimize = 30

    fun set(
        operandStackMaxSize: Int,
        recursiveLimit: Int,
        callingStackOptimize: Int
    ) {
        this.operandStackMaxSize = operandStackMaxSize
        this.recursiveLimit = recursiveLimit
        this.callingStackOptimize = callingStackOptimize
    }

    override fun toString(): String {
        return "IVMProperties(operandStackMaxSize=$operandStackMaxSize, recursiveLimit=$recursiveLimit)"
    }
}
