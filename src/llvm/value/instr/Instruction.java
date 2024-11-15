package llvm.value.instr;

import llvm.types.DataIrTy;
import llvm.value.User;
import llvm.value.Value;

import java.util.ArrayList;

public class Instruction extends User {
    public Instruction(String name, DataIrTy dataIrTy, ArrayList<Value> operands) {
        super(name, dataIrTy, operands);
    }
    
    public Instruction(String name, DataIrTy dataIrTy) {
        super(name, dataIrTy);
    }
    
    public Instruction(DataIrTy dataIrTy) {
        super(dataIrTy);
    }
}
