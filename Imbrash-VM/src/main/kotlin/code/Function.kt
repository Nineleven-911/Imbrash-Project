package hairinne.ip.vm.code

data class Function(val id: Long, val start: Int, val end: Int) {
    fun getRange(): IntRange {
        return start until end
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
