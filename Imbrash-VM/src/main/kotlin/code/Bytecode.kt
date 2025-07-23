package hairinne.ip.vm.code

import hairinne.ip.vm.vm.ExecutionUnit
import java.text.SimpleDateFormat
import java.util.*

object Bytecode {
    const val PUSH     : Byte = 0x00 /* Labels: 0-3 */
    const val POP      : Byte = 0x01 /* Labels: 0-3 */
    const val PRT      : Byte = 0x02 /* Labels: 0-3 */
    const val RET      : Byte = 0x03 /* Labels: 0-4 */
    const val CALL     : Byte = 0x04 /* Labels: 0-4 */
    const val PRT_C    : Byte = 0x05
    const val BINARY_OP: Byte = 0x06 /* Look at object: BinaryOperator */
    const val GOTO     : Byte = 0x07
    const val IF       : Byte = 0x08 /* Look at object: IfConditionalJump */

    fun labelTransfer(label: Byte): Int {
        return 1 shl label.toInt()
    }

    fun labelTransfer(label: Int): Int {
        return 1 shl label
    }

    fun labelRetransfer(byteCount: Int): Byte {
        return when (byteCount) {
            1 -> 0
            2 -> 1
            4 -> 2
            8 -> 3
            else -> throw IllegalArgumentException("Invalid byte count: $byteCount")
        }
    }
}

object BinaryOperator {
    const val ADD: Byte = 0x00
    const val SUB: Byte = 0x01
    const val MUL: Byte = 0x02
    const val DIV: Byte = 0x03
    const val MOD: Byte = 0x04
}

object IfConditionalJump {
    const val EQ: Byte = 0x00 // ==
    const val NE: Byte = 0x01 // !=
    const val LT: Byte = 0x02 // <
    const val GE: Byte = 0x03 // >=
    const val GT: Byte = 0x04 // >
    const val LE: Byte = 0x05 // <=

    object Integer {
        const val CMP_EQ: Byte = 0x06
        const val CMP_NE: Byte = 0x07
        const val CMP_LT: Byte = 0x08
        const val CMP_GE: Byte = 0x09
        const val CMP_GT: Byte = 0x0A
        const val CMP_LE: Byte = 0x0B
    }
}

object Debugs {
    const val DEBUG: Byte = -0x01

    const val INFO: Byte = 0
    const val WARNING: Byte = 1
    const val ERROR: Byte = 2

    const val PRT_PC: Byte = 0x01
    const val PRT_STACK: Byte = 0x02
    const val PRT_CALLING_STACK: Byte = 0x04

    fun debug(eu: ExecutionUnit): Int {
        val frame = eu.stack.peek()
        var ptr = frame.pc
        // Mach Debug Level
        when (eu[ptr++]) {
            INFO -> print("DEBUG: [Info] ")
            WARNING -> print("DEBUG: [Warning] ")
            ERROR -> print("DEBUG: [Error] ")
            else -> print("DEBUG: [NULL] ")
        }
        // Print time e.g. 07-22 14:40:32
        val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault()!!)
        print("at ${timeFormatter.format(Date(System.currentTimeMillis()))}: ")
        // Match Debug Behavior
        when (eu[ptr++]) {
            PRT_PC -> println("Program Counter = ${frame.pc - 1}, Next is ${frame.pc}")
            PRT_STACK -> println("Operand Stack: $frame")
            PRT_CALLING_STACK -> println("Calling Stack: ${eu.stack}")
        }
        return ptr
    }
}
