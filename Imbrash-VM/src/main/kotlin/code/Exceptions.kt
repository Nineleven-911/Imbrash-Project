package hairinne.ip.vm.code

import hairinne.ip.vm.vm.ExecutionUnit
import hairinne.utils.Overlooks

open class IVMBaseException(
    eu: ExecutionUnit,
    details: String,
    verbose: Boolean = false,
    possibleReason: String = "BaseException should never be threw. Please check your IVM code."
) : Throwable() {

    private val template: String =
        """
            Stopping!
            An error has occurred:
            ErrorType: ${this::class.simpleName}
            Possible Reason: $possibleReason
            Details: $details
        """.trimIndent() + "\n" + if (verbose) """
            VM Properties:
              Function Call Stack: ${
                  if (eu.stack.isEmpty()) "[]" 
                  else eu.stack.toList()
              }
              Executing Program Counter: ${if (eu.stack.isEmpty()) "[ No Programs In EU ]" else eu.stack.peek().pc}
              Executing Program Operand Stack: ${
            if (eu.stack.isEmpty()) "[ No Programs In EU ]"
            else Overlooks.list(eu.stack.peek().getStack().toList())
        }
            
            VM Code:
              ${Overlooks.list(eu.module.code)}
        """.trimIndent() + "\n" else "\n"

    init {
        System.err.println(template)
    }
}


class EntrypointNotFoundException(
    eu: ExecutionUnit,
    details: String,
    verbose: Boolean = false
) : IVMBaseException(
    eu, details, verbose,
    "Cannot find entrypoint in code. Your ICompiler may have some problem."
)

class IterableOutOfRangeException(
    eu: ExecutionUnit,
    details: String,
    verbose: Boolean = false
) : IVMBaseException(
    eu, details, verbose,
    "Iterable out of range. Please check your code."
)
