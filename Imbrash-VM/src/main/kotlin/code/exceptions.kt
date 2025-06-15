package hairinne.ip.vm.code

import kotlin.system.exitProcess

open class IVMBaseException(details: String) {
    open val exitCode = 1
    open val message =
        "BaseException should never be threw. Please check your IVM code."
    private val template = """
            Stopping!
            An error has occurred:
            Error:
              ExitCode: $exitCode
              Type:
                ${this::class.simpleName}
              
              Message:
                $message
              
              Details:
                $details
        """.trimIndent()
    init {
        System.err.println(template)
        exitProcess(exitCode)
    }
}

class EntryPointNotFoundException(details: String) : IVMBaseException(details) {
    override val exitCode: Int get() = 2
    override val message: String get() =
        "Cannot find entrypoint in code. Your ICompiler may have some problem."
}
