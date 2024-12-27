package llvm.value.instr;

import llvm.types.VoidIrTy;
import llvm.value.Value;
import llvm.value.notInstr.BasicBlock;

/**
 * 将 br 指令分成了两个子指令。
 * br label <dest>
 */
public class Jump extends Instruction {
    public Jump(Value host, BasicBlock targetBb) {
        super(host, new VoidIrTy());
        addOperand(targetBb);
    }
    
    @Override
    public String toString() {
        return  "br label " + getOperand(0).getName();
    }
}
