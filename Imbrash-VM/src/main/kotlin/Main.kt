package hairinne.ip.vm

import hairinne.ip.vm.code.Bytecode
import hairinne.ip.vm.code.CodeConstructor
import hairinne.ip.vm.code.Module
import hairinne.ip.vm.vm.ExecutionUnit

fun main() {
    val executionUnit = ExecutionUnit(
        Module(
            CodeConstructor()
                .add(
                    Bytecode.POP, 1
                )
                .ret()
                .build()
        ),
        arrayOf()
    )
    executionUnit.execute()
}
