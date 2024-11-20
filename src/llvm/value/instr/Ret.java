package llvm.value.instr;

import llvm.types.DataIrTy;
import llvm.types.VoidIrTy;
import llvm.value.Value;

/**
 * 根据是否返回值可以分为两种情况:
 *   ret <type> <value>
 *   ret void
 */
public class Ret extends Instruction {
    private final boolean hasReturnValue;
    // 有返回值
    public Ret(Value returnValue) {
        super((DataIrTy) returnValue.getType());
        addOperand(returnValue);
        hasReturnValue = true;
    }
    
    // 无返回值
    public Ret() {
        super(new VoidIrTy());
        hasReturnValue = false;
    }
    
    public boolean hasReturnValue() {
        return hasReturnValue;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ret ");
        if (hasReturnValue) {
            Value value = getOperand(0);
            sb.append(value.getType());
            sb.append(" ");
            sb.append(value.getName());
        } else  {
            sb.append("void");
        }
        return sb.toString();
    }
}
