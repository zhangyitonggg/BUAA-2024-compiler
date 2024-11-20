package backend.Instruction.compute;

import backend.Register.Reg;

public class Addu extends ComputeM {
    public Addu(Reg to, Reg op1, Reg op2) {
        super(to, op1, op2);
    }
    
    @Override
    public String toString() {
        return super.toString("addu");
    }
}
