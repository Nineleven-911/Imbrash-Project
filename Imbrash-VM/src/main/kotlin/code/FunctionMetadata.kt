package hairinne.ip.vm.code

data class FunctionLocation(val start: Long, val end: Long)

data class FunctionID(val name: String, val id: Long)

data class Function(val id: FunctionID, val location: FunctionLocation)
