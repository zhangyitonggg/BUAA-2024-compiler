package llvm.value.instr;

import llvm.types.DataIrTy;
import llvm.value.User;
import llvm.value.Value;

import java.util.ArrayList;

public class Instruction extends User {
    public Instruction(Value host, String name, DataIrTy dataIrTy, ArrayList<Value> operands) {
        super(host, name, dataIrTy, operands);
    }
    
    public Instruction(Value host, String name, DataIrTy dataIrTy) {
        super(host, name, dataIrTy);
    }
    
    public Instruction(Value host, DataIrTy dataIrTy) {
        super(host, dataIrTy);
    }
}
