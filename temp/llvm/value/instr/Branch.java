package llvm.value.instr;

import llvm.types.VoidIrTy;
import llvm.value.Value;
import llvm.value.notInstr.BasicBlock;

/**
 * 将 br 指令分成了两个子指令。
 * br i1 <cond>, label <iftrue>, label <iffalse>
 */
public class Branch extends Instruction {
    public Branch(Value host, Value cond, BasicBlock trueBb, BasicBlock falseBb) {
        super(host, new VoidIrTy());
        addOperand(cond);
        addOperand(trueBb);
        addOperand(falseBb);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("br i1 ");
        sb.append(getOperand(0).getName());
        sb.append(", label ");
        sb.append(getOperand(1).getName());
        sb.append(", label ");
        sb.append(getOperand(2).getName());
        return sb.toString();
    }
}
