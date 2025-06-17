package hairinne.ip.vm

import hairinne.ip.vm.code.Bytecode
import hairinne.ip.vm.code.Module
import hairinne.ip.vm.vm.ExecutionUnit

fun main() {
    println("""
      TestCode:
        push 1
        push 2
        push 3
        push 4
        prt
        push 5
        prt
        pop
        prt
        pop
        prt
        pop
        prt
        ret
    """.trimIndent())
    val executionUnit = ExecutionUnit(
        Module(
            mutableListOf(
                Bytecode.PUSH, 0, 1,
                Bytecode.PUSH, 0, 2,
                Bytecode.PUSH, 0, 3,
                Bytecode.PUSH, 0, 4,
                Bytecode.PRT, 0,
                Bytecode.PUSH, 0, 5,
                Bytecode.PRT, 0,
                Bytecode.POP, 0,
                Bytecode.PRT, 0,
                Bytecode.POP, 0,
                Bytecode.PRT, 0,
                Bytecode.POP, 0,
                Bytecode.PRT, 0,
                Bytecode.RET, 0
            )
        ),
        arrayOf()
    )
    executionUnit.execute()
}
