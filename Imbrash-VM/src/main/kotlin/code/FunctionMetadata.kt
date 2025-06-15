package hairinne.ip.vm.code

data class FunctionLocation(val start: Int, val end: Int) {
    /**
     * Location range is: [start, end)
     */
    fun toRange(): IntRange {
        return start until end
    }
}

data class FunctionID(val name: String, val id: Long)

data class Function(val id: FunctionID, val location: FunctionLocation)
