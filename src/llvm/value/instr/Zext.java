package llvm.value.instr;

import llvm.types.DataIrTy;
import llvm.value.Value;

/**
 * <result> = zext <ty> <value> to <ty2>
 */
public class Zext extends Instruction {
    public Zext(Value host, int nameCount, Value oriValue, DataIrTy targetTy) {
        super(host, "%v" + nameCount, targetTy);
        addOperand(oriValue);
    }
    
    public Value getOriValue() {
        return getOperand(0);
    }
    
    @Override
    public String toString() {
        return this.getName() + " = zext " + getOriValue().getType() + " " +
                    getOriValue().getName() + " to " + this.getType();
    }
}
