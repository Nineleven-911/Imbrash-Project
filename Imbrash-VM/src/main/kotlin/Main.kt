package hairinne.ip.vm

import hairinne.ip.vm.code.BinaryOperator
import hairinne.ip.vm.code.Bytecode
import hairinne.ip.vm.code.CodeConstructor
import hairinne.ip.vm.code.Module
import hairinne.ip.vm.vm.ExecutionUnit

fun main() {
    val executionUnit = ExecutionUnit(
        Module(
            CodeConstructor()
                .add(Bytecode.PUSH, 0, 0xff)
                .add(Bytecode.PUSH, 0, 0xfe)
                .add(Bytecode.BINARY_OP, BinaryOperator.ADD)
                .add(Bytecode.PRT, 0)
                .ret()
                .build()
        ),
        arrayOf() // Not implemented yet
    )
    // This Code will calculate (0xFF + 0xFE).toByte(), result is -3
    executionUnit.execute()
}
