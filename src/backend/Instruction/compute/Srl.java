package backend.Instruction.compute;

import backend.Register.Reg;

public class Srl extends ComputeM {
    public Srl(Reg to, Reg op1, Reg op2) {
        super(to, op1, op2);
    }
    
    @Override
    public String toString() {
        return super.toString("srl");
    }
}
