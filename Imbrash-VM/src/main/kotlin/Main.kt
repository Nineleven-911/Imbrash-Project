package hairinne.ip.vm

import hairinne.ip.vm.code.Bytecode
import hairinne.ip.vm.code.CodeConstructor
import hairinne.ip.vm.code.Module
import hairinne.ip.vm.vm.ExecutionUnit

fun main() {
    val executionUnit = ExecutionUnit(
        Module(
            CodeConstructor().add( // 0x4f60, 0x597d, 0x4e16, 0x754c, 0xff01
                Bytecode.PUSH, 1, '你'.code,
                Bytecode.PRT_C, 2,
                Bytecode.PUSH, 1, '好'.code,
                Bytecode.PRT_C, 2,
                Bytecode.PUSH, 1, '世'.code,
                Bytecode.PRT_C, 2,
                Bytecode.PUSH, 1, '界'.code,
                Bytecode.PRT_C, 2,
                Bytecode.PUSH
            ).add(0).add(
                '!'.code,
                Bytecode.PRT_C, 1,
                Bytecode.RET
            ).build()
        ),
        arrayOf()
    )
    executionUnit.execute()
}
