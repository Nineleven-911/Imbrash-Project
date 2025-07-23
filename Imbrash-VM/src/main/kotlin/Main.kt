package hairinne.ip.vm

import hairinne.ip.vm.code.BinaryOperator
import hairinne.ip.vm.code.Bytecode
import hairinne.ip.vm.code.CodeConstructor
import hairinne.ip.vm.code.Module
import hairinne.ip.vm.vm.ExecutionUnit
import hairinne.ip.vm.vm.VMProperties
import hairinne.utils.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    initialize(args.toList())
    val constructor = (CodeConstructor()
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
        .add(Bytecode.PRT, 0)
        .add(Bytecode.PUSH, 2, 0, 0, 0, '\n'.code)
        .add(Bytecode.PRT_C)
        .ret(0)
    )

    val module = Module(
        constructor.build()
    )

    val executionUnit = ExecutionUnit(
        module,
        constructor.getFunctions().toTypedArray()
    )

    // This Code will calculate (0xFF + 0xFE).toByte(), result is -3
    executionUnit.execute()
}

fun initialize(args: List<String>) {
    println("Start with arguments: $args")
    val parser = ArgumentParser()
    parser.extends(
        Argument(
            listOf("--operand-stack-max-size", "-osms", "-operand-stack-max"),
            "OperandStackMaxSize",
            Action.STORE_INT,
            default = 4
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
            throw ArgumentNotFoundException("Must specify PackageName or ModuleName")
        }

        VMProperties.set(
            parsedArgs.getIfNotNull<Int>("OperandStackMaxSize") shl 20,
            parsedArgs.getIfNotNull<Int>("RecursiveLimit"),
        )
    } catch (e: Throwable) {
        println(e.message)
        println(e.stackTrace)
        println(ResourceReader.readFile("vm/ArgumentUsage.txt"))
        exitProcess(0)
    }
}
