package hairinne.ip.vm.code

object Bytecode {
    const val PUSH: Byte = 0x00 /* Labels: 0-3 */
    const val POP : Byte = 0x01 /* Labels: 0-3 */
    const val PRT : Byte = 0x02 /* Labels: 0-3 */
    const val RET : Byte = 0x03 /* Labels: 0-4 */
    const val CALL: Byte = 0x04
}
