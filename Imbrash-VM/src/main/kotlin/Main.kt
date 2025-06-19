package hairinne.ip.vm

import hairinne.ip.vm.code.Bytecode
import hairinne.ip.vm.code.CodeConstructor
import hairinne.ip.vm.code.Module
import hairinne.ip.vm.vm.ExecutionUnit

fun main() {
    val executionUnit = ExecutionUnit(
        Module(
            CodeConstructor().add(
                Bytecode.PRT_C, 1, 0x4f60,
                Bytecode.PRT_C, 1, 0x597d,
                Bytecode.PRT_C, 1, 0x4e16,
                Bytecode.PRT_C, 1, 0x754c,
                Bytecode.PRT_C, 1, 0xff01,
                Bytecode.RET
            ).build()
        ),
        arrayOf()
    )
    print(executionUnit.module)
    executionUnit.execute()
}