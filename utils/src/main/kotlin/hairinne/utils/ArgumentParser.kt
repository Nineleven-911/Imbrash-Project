package hairinne.utils

import kotlin.system.exitProcess

enum class Action {
    STORE_TRUE,
    STORE_FALSE,
    STORE_STRING,
    STORE_INT,
    STORE_FLOAT,
    STORE_DOUBLE,
    STORE_LONG
}

data object NotProvided

data class Argument(
    val flags: List<String>,
    val name: String = "",
    val action: Action,
    val default: Any? = NotProvided,
    val required: Boolean = false
)

class ArgumentNotFoundException(key: String, extras: String = ""):
    Exception("Unexpected argument $key. $extras")

data class ParsedArgs(
    val args: Map<String, Any?>,
    val systemProperties: Map<String, String?>
) {
    inline fun<reified T> get(key: String): T? {
        return args[key] as T?
    }

    inline fun<reified T> getIfNotNull(
        key: String,
        default: () -> T = {
            throw ArgumentNotFoundException(
                key,
                "There's not such argument in line. " +
                        "This should not be occur in the case of arguments has default value."
            )
        }
    ): T {
        val value = args[key]
        if (value is NotProvided)
            return default()
        return value as T
    }

    fun exists(key: String): Boolean {
        return args.containsKey(key) && args[key] !is NotProvided
    }

    fun getSystemProperty(
        key: String,
        default: () -> String = {
            throw ArgumentNotFoundException(
                key,
                "There's not such argument in line. " +
                        "This should not be occurred in the case of arguments has default value."
            )
        }
    ): String {
        // matches pair system property ( abc: xyz ) == ( -sp abc=xyz )
        if (!systemProperties.containsKey(key)) {
            return default()
        }
        return systemProperties[key]!!
    }

    fun existsSystemProperty(key: String): Boolean {
        // matches boolean system property ( abc: null ) == ( -sp abc )
        return systemProperties.containsKey(key) && systemProperties[key] == null
    }
}

class ArgumentParser(val argRules: MutableList<Argument> = mutableListOf()) {
    fun add(argument: Argument) {
        argRules.add(argument)
    }

    fun extends(vararg arguments: Argument) {
        argRules.addAll(arguments)
    }

    fun parseArgs(args: List<String>): ParsedArgs {
        val parsedArgs = mutableMapOf<String, Any?>()
        val systemProperties = mutableMapOf<String, String?>()

        if (args.map {
                argRules.find { it1 ->
                    it1.flags.containsAll(listOf("-h", "--help"))
                }!!.flags.contains(it)
            }.contains(true)) {
            throw Exception()
        }

        if (args.map { it == "--version" }.contains(true)) {
            println(ResourceReader.readFile("initials/vm/version.txt"))
            exitProcess(0)
        }
        var ptr = 0
        while (ptr < args.size) {
            val key = args[ptr++]
            if (key == "-sp") {
                val property = args[ptr++]
                if (property.contains("=")) {
                    val (propertyName, propertyValue) = property.split("=")
                    systemProperties[propertyName] = propertyValue
                } else {
                    systemProperties[property] = null
                }
                continue
            }
            val argRule = argRules.firstOrNull {
                it.flags.contains(key)
            } ?: throw ArgumentNotFoundException(key, "There are no any argument rules about this key.")
            when (argRule.action) {
                Action.STORE_TRUE -> {
                    parsedArgs[argRule.name] = true
                }
                Action.STORE_FALSE -> {
                    parsedArgs[argRule.name] = false
                }
                Action.STORE_STRING -> {
                    parsedArgs[argRule.name] = args[ptr++]
                }
                Action.STORE_INT -> {
                    parsedArgs[argRule.name] = args[ptr++].toInt()
                }
                Action.STORE_FLOAT -> {
                    parsedArgs[argRule.name] = args[ptr++].toFloat()
                }
                Action.STORE_DOUBLE -> {
                    parsedArgs[argRule.name] = args[ptr++].toDouble()
                }
                Action.STORE_LONG -> {
                    parsedArgs[argRule.name] = args[ptr++].toLong()
                }
            }
        }

        for (argRule in argRules) {
            if (argRule.required && parsedArgs[argRule.name] == null) {
                throw ArgumentNotFoundException(argRule.name, "This argument is required.")
            }
            if (!argRule.required && parsedArgs[argRule.name] == null) {
                parsedArgs[argRule.name] = argRule.default
            }
            if (argRule.default != null && parsedArgs[argRule.name] == null) {
                parsedArgs[argRule.name] = argRule.default
            }
        }

        return ParsedArgs(parsedArgs, systemProperties)
    }
}
