package llvm.value.instr;

import llvm.types.DataIrTy;
import llvm.types.PointerIrTy;
import llvm.value.Value;

public class Load extends Instruction{
    public Load(int nameCount, Value pointer) {
        super("%v" + nameCount, (DataIrTy)(((PointerIrTy) pointer.getType()).deRefIrTy));
        addOperand(pointer);
    }
    
    @Override
    public String toString() {
        Value pointer = getOperand(0);
        return getName() + " = load " + getType() + ", " + pointer.getType() + " " + pointer.getName();
    }
}
