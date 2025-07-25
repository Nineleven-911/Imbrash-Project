package hairinne.ip.vm.code

import hairinne.ip.vm.vm.ExecutionUnit
import hairinne.ip.vm.vm.VMProperties

open class IVMBaseException(
    eu: ExecutionUnit?,
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
            
        """.trimIndent() +
        if (eu != null)
        """
            VM Properties:
              FunctionMeta Call Stack: ${
                  if (eu.stack.isEmpty()) "[]" 
                  else { 
                      val a = eu.stack.toMutableList()
                      if (VMProperties.callingStackOptimize == -1)
                          a
                      else {
                          val optimized = a.size > VMProperties.callingStackOptimize
                          while (a.size > VMProperties.callingStackOptimize) a.removeLast()
                          "[${a.joinToString(", ")}" + (if (optimized) ", And ${
                              eu.stack.size - VMProperties.callingStackOptimize
                          } more..." else "") + "]"
                      }
                  }
              }
              Executing Program Counter: ${if (eu.stack.isEmpty()) "[ No Programs In EU ]" else eu.stack.peek().pc}
              Executing Program Operand Stack: ${
            if (eu.stack.isEmpty()) "[ No Programs In EU ]"
            else eu.stack.peek().toString()
            }
            
            ${eu.getStackTrace(16)}
        """.trimIndent() + "\n"
    else "No ExecutionUnit Provided."

    init {
        System.err.println(template)
    }
}


class EntrypointNotFoundException(
    eu: ExecutionUnit?,
    details: String,
) : IVMBaseException(
    eu, details,
    "Cannot find entrypoint in code. Your ICompiler may have some problem."
)

class IterableOutOfBoundsException(
    eu: ExecutionUnit?,
    details: String,
) : IVMBaseException(
    eu, details,
    "Iterable out of range. Please check your code."
)

class EmptyOperandStackException(
    eu: ExecutionUnit?,
    details: String,
) : IVMBaseException(
    eu, details,
    "Stack is empty. Check where is using an empty stack."
)

class InvalidDataException(
    eu: ExecutionUnit?,
    details: String,
) : IVMBaseException(
    eu, details,
    "Invalid data. Usually occurred on PRT_C."
)

class BytecodeNotFoundException(
    eu: ExecutionUnit?,
    details: String,
) : IVMBaseException(
    eu, details,
    "Invalid bytecode."
)

class RuntimeException(
    eu: ExecutionUnit?,
    details: String,
) : IVMBaseException(
    eu, details,
    "An error occurred during runtime."
)

class RecursionTooDeepException(
    eu: ExecutionUnit?,
    details: String,
) : IVMBaseException(
    eu, details,
    "Recursion limit exceeded."
)

class NoSuchVariableException(
    eu: ExecutionUnit?,
    details: String,
) : IVMBaseException(
    eu, details,
    "There's no such local variable in this scope."
)
