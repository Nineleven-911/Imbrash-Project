package hairinne.ip.vm.code

import hairinne.ip.vm.vm.ExecutionUnit

open class IVMBaseException(
    eu: ExecutionUnit,
    details: String,
    isVerbose: Boolean = false
) : Throwable() {
    open val tip =
        "BaseException should never be threw. Please check your IVM code."

    private val template by lazy {
        """
            Stopping!
            An error has occurred:
            ErrorType: ${this::class.simpleName}
            Message: $tip
            Details: $details
        """.trimIndent() + "\n" + if (isVerbose) """
            VM Properties:
              Function Call Stack: ${if (eu.stack.isEmpty()) "[]" else eu.stack.joinToString(", ")}
              Executing Program Counter: ${if (eu.stack.isEmpty()) "[ No Programs In EU ]" else eu.stack.peek().pc}
              Executing Program Operand Stack: ${
            if (eu.stack.isEmpty()) "[ No Programs In EU ]"
            else eu.stack.peek().operandStack.stack.toList()
        }
        """.trimIndent() + "\n" else "\n"
    }

    init {
        System.err.println(template)
    }
}

class EntryPointNotFoundException(
    eu: ExecutionUnit,
    details: String,
    isVerbose: Boolean = false
) : IVMBaseException(eu, details, isVerbose) {
    override val tip: String =
        "Cannot find entrypoint in code. Your ICompiler may have some problem."
}
