package hairinne.ip.vm

import hairinne.ip.vm.code.EntryPointNotFoundException
import hairinne.ip.vm.code.Function
import hairinne.ip.vm.vm.ExecutionUnit
import hairinne.ip.vm.code.Module
import hairinne.ip.vm.code.FunctionLocation
import hairinne.ip.vm.code.FunctionID

fun main() {
    val eu = ExecutionUnit(
        Module(mutableListOf(0.toByte())),
        arrayOf(Function(FunctionID("main", 0), FunctionLocation(0, 0)))
    )
    throw EntryPointNotFoundException(eu, "", true)
}
