package hairinne.ip.vm

import hairinne.ip.vm.code.Bytecode
import hairinne.ip.vm.code.CodeConstructor
import hairinne.ip.vm.code.If
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
    val constructor = (CodeConstructor()
        .function(0, "main")
        .add(Bytecode.PUSH, 0, 3)
        .add(Bytecode.CALL, 0, 0, 0, 0, 0, 0, 0, 1, 1)
        .ret(0)
        .function(1, "aaa")
        .add(Bytecode.PUSH, 0, 1)
        .add(Bytecode.IF, If.Integer.CMP_GT, 0,0,0,0/* Addr Here*/)
        .add()
        .ret(0)
    )

    val constructor2 = CodeConstructor()
        .function(0, "main")
        .add(Bytecode.CALL, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        .ret(0)

    val c = constructor2
    val module = Module(
        c.build(),
        c.getFunctions().toList()
    )

    module.disassembledFT("DA.txt")
    module.disassembledCS("DA.txt", true)

    val executionUnit = ExecutionUnit(module)
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
        ), Argument(
            listOf("--cs-optimize-omissions", "--cs-opt-om", "-co"),
            "CallingStackOptimize",
            Action.STORE_INT,
            default = 30
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
            parsedArgs.getIfNotNull<Int>("CallingStackOptimize")
        )
    } catch (e: Throwable) {
        e.printStackTrace()
        System.err.println(ResourceReader.readFile("vm/ArgumentUsage.txt"))
        exitProcess(0)
    }
}
