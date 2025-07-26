package hairinne.ip.vm.code

object FunctionType {
    const val METHOD: Byte = 0
    const val FUNCTION: Byte = 1
}

data class FunctionMeta(
    val id: Long,
    val start: Int,
    val end: Int,
    val variableLength: Int,

    val name: String,
    // val type: Byte
) {
    fun getRange(): IntRange {
        return start until end
    }

    override fun toString(): String {
        return "FunctionMeta at [$start, $end) Meta(Identifier=$id; Name=\"$name\", VariableLength=$variableLength)"
    }
}

fun getFunction(
    functions: Array<FunctionMeta>,
    id: Long,
    notFound: (Long) -> Throwable = {
        EntrypointNotFoundException(null, "There's no function id is $id")
    }
): FunctionMeta {
    for (function in functions)
        if (function.id == id)
            return function
    throw notFound(id)
}
