package hairinne.ip.vm.code

object Bytecode {
    const val PUSH  : Byte = 0x00 /* Labels: 0-3 */
    const val POP   : Byte = 0x01 /* Labels: 0-3 */
    const val PRT   : Byte = 0x02 /* Labels: 0-3 */
    const val RET   : Byte = 0x03 /* Labels: 0-4 */
    const val CALL  : Byte = 0x04 /* Labels: 0-4 */
    const val PRT_C : Byte = 0x05

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
