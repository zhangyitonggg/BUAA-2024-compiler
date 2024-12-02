package llvm.value.instr;

import llvm.types.VoidIrTy;
import llvm.value.Value;

public class Copy extends Instruction {
    public Copy(Value host, Value target, Value from) {
        super(host, new VoidIrTy());
        addOperand(target);
        addOperand(from);
    }
    
    public Value getTarget() {
        return getOperand(0);
    }
    
    public Value getFrom() {
        return getOperand(1);
    }
    
    public void setFrom(Value replace) {
        if (replace == null) {
            return;
        }
        replace.addUse(this); // 不过也不做其他优化了，似乎没啥意义
        getAllOperands().set(1, replace);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("copy ");
        sb.append(getTarget().getName());
        sb.append(", ");
        sb.append(getFrom().getName());
        return sb.toString();
    }
}
