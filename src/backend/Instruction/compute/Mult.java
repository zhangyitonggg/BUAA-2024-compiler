package backend.Instruction.compute;

import backend.Register.Reg;

public class Mult extends ComputeM {
    public Mult(Reg op1, Reg op2) {
        super(null, op1, op2);
    }
    
    @Override
    public String toString() {
        return super.toString("mult");
    }
}
