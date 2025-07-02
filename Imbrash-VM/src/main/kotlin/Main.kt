package hairinne.ip.vm

import hairinne.ip.vm.code.Bytecode
import hairinne.ip.vm.code.CodeConstructor
import hairinne.ip.vm.code.Module
import hairinne.ip.vm.vm.ExecutionUnit

fun main() {
    val executionUnit = ExecutionUnit(
        Module(
            CodeConstructor()
                .add(Bytecode.PUSH, 0, 0xff)
                .add(Bytecode.PUSH, 0, 0xfd)
                .add(Bytecode.BINARY_OP, 0, 0x01)
                .add(Bytecode.PRT,0)
                .ret()
                .build()
        ),
        arrayOf()
    )
    executionUnit.execute()
}
