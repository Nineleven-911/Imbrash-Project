package hairinne.ip.vm.code

import hairinne.ip.vm.vm.ExecutionUnit
import hairinne.utils.Overlooks

open class IVMBaseException(
    eu: ExecutionUnit,
    details: String,
    possibleReason: String = "BaseException should never be threw. Please check your IVM code."
) : Throwable() {

    private val template: String =
        """
            Stopping!
            An error has occurred:
            ErrorType: ${this::class.simpleName}
            Possible Reason: $possibleReason
            Details: $details
        
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
              ${eu.module}
        """.trimIndent() + "\n"

    init {
        System.err.println(template)
    }
}


class EntrypointNotFoundException(
    eu: ExecutionUnit,
    details: String,
) : IVMBaseException(
    eu, details,
    "Cannot find entrypoint in code. Your ICompiler may have some problem."
)

class IterableOutOfRangeException(
    eu: ExecutionUnit,
    details: String,
) : IVMBaseException(
    eu, details,
    "Iterable out of range. Please check your code."
)

class EmptyStackException(
    eu: ExecutionUnit,
    details: String,
) : IVMBaseException(
    eu, details,
    "Stack is empty. Check where is using an empty stack."
)

class InvalidDataException(
    eu: ExecutionUnit,
    details: String,
) : IVMBaseException(
    eu, details,
    "Invalid data. Usually occurred on PRT_C."
)
