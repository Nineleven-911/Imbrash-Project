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
                    Bytecode.PUSH, 0, 'H'.code,
                    Bytecode.PRT_C, 1,
                    Bytecode.PUSH, 0, 'e'.code,
                    Bytecode.PRT_C, 1,
                    Bytecode.PUSH, 0, 'l'.code,
                    Bytecode.PRT_C, 1,
                    Bytecode.PUSH, 0, 'l'.code,
                    Bytecode.PRT_C, 1,
                    Bytecode.PUSH, 0, 'o'.code,
                    Bytecode.PRT_C, 1,
                    Bytecode.PUSH, 0, ','.code,
                    Bytecode.PRT_C, 1,
                    Bytecode.PUSH, 0, ' '.code,
                    Bytecode.PRT_C, 1,
                    Bytecode.PUSH, 0, 'W'.code,
                    Bytecode.PRT_C, 1,
                    Bytecode.PUSH, 0, 'o'.code,
                    Bytecode.PRT_C, 1,
                    Bytecode.PUSH, 0, 'r'.code,
                    Bytecode.PRT_C, 1,
                    Bytecode.PUSH, 0, 'l'.code,
                    Bytecode.PRT_C, 1,
                    Bytecode.PUSH, 0, 'd'.code,
                    Bytecode.PRT_C, 1,
                    Bytecode.PUSH, 0, '!'.code,
                    Bytecode.PRT_C, 1
                )
                .ret()
                .build()
        ),
        arrayOf()
    )
    executionUnit.execute()
}
