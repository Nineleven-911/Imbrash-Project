package hairinne.ip.vm.code

data class Function(val id: Long, val start: Int, val end: Int, val name: String) {
    fun getRange(): IntRange {
        return start until end
    }

    override fun toString(): String {
        return "Function at [$start, $end) Meta(Identifier=$id; Name=$name)"
    }
}

fun getFunction(
    functions: Array<Function>,
    id: Long,
    notFound: (Long) -> Throwable = { EntrypointNotFoundException(null, "There's no function id is $id") }): Function {
    for (function in functions)
        if (function.id == id)
            return function
    throw notFound(id)
}
