package llvm.value.instr;

import llvm.types.VoidIrTy;
import llvm.value.Value;

/**
 * store <ty> <value>, ptr <pointer>
 */
public class Store extends Instruction {
    public Store(Value value, Value pointer) {
        super(new VoidIrTy());
        addOperand(value);
        addOperand(pointer);
    }
    
    @Override
    public String toString() {
        Value value = getOperand(0);
        Value pointer = getOperand(1);
        return "store " + value.getType() + " " + value.getName() + ", " + pointer.getType() + " " + pointer.getName();
    }
}
