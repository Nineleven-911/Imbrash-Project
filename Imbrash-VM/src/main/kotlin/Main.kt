package hairinne.ip.vm

import hairinne.ip.vm.code.BinaryOperator
import hairinne.ip.vm.code.Bytecode
import hairinne.ip.vm.code.CodeConstructor
import hairinne.ip.vm.code.Module
import hairinne.ip.vm.vm.ExecutionUnit
import hairinne.ip.vm.vm.VMProperties
import hairinne.utils.Action
import hairinne.utils.Argument
import hairinne.utils.ArgumentParser
import hairinne.utils.ResourceReader
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    initialize(args.toList())
    val constructor1 = (CodeConstructor()
        .function(0) // Function Main
        .add(Bytecode.PUSH, 0, 0xFF)
        .add(Bytecode.PUSH, 0, 0xFE)
        .add(Bytecode.CALL, 0, 0, 0, 0, 0, 0, 0, 1, 2)
        .add(Bytecode.PUSH, 0, 0xFF)
        .add(Bytecode.PUSH, 0, 0x01)
        .add(Bytecode.CALL, 0, 0, 0, 0, 0, 0, 0, 1, 2)
        .ret(0)
        .function(1) // Function Add&Print
        .add(Bytecode.BINARY_OP, BinaryOperator.ADD)
        .printf("💗杂鱼~💗杂鱼~ 主人真是个杂鱼~💗")
        .add(Bytecode.PRT, 0)
        .printf("\n")
        .ret(0)
    )

    val constructor2 = (CodeConstructor()
        .function(0) // Function Main
        .add(Bytecode.CALL, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        .ret(0)
    )
    val module = Module(
        constructor1.build()
    )

    module.disassembledCS("C:\\Users\\AW\\Desktop\\DA.txt")

    val executionUnit = ExecutionUnit(
        module,
        constructor1.getFunctions().toTypedArray()
    )
    executionUnit.execute()
}

fun initialize(args: List<String>) {
    val parser = ArgumentParser()
    parser.extends(
        Argument(
            listOf("--operand-stack-max-size", "-osms", "-operand-stack-max"),
            "OperandStackMaxSize",
            Action.STORE_INT,
            default = 4096
        ), Argument(
            listOf("--call-stack-deep", "-csd", "--recursive-limit", "-rl"),
            "RecursiveLimit",
            Action.STORE_INT,
            default = 1024
        ), Argument(
            listOf("--debug", "-d"),
            "Debug",
            Action.STORE_TRUE
        ), Argument(
            listOf("--package", "-p"),
            "PackageName",
            Action.STORE_STRING
        ), Argument(
            listOf("--module", "-mod", "-m"),
            "ModuleName",
            Action.STORE_STRING
        )
    )

    try {
        val parsedArgs = parser.parseArgs(args)

        if (!parsedArgs.exists("PackageName") && !parsedArgs.exists("ModuleName")) {
            // throw ArgumentNotFoundException("Must specify PackageName or ModuleName")
        }

        VMProperties.set(
            parsedArgs.getIfNotNull<Int>("OperandStackMaxSize") shl 10,
            parsedArgs.getIfNotNull<Int>("RecursiveLimit"),
        )
    } catch (e: Throwable) {
        e.printStackTrace()
        System.err.println(ResourceReader.readFile("vm/ArgumentUsage.txt"))
        exitProcess(0)
    }
}
