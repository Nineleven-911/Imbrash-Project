package hairinne.ip.vm.stack

import hairinne.ip.vm.stack.VirtualMachineStack

class StackFrame(var pc: Int = 0) {
    var operandStack: VirtualMachineStack = VirtualMachineStack()
    var localVariables: MutableMap<Long, Long> = mutableMapOf()
}