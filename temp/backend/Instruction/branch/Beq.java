package backend.Instruction.branch;

import backend.Register.Reg;

public class Beq extends BranchM {
    public Beq(Reg lReg, Reg rReg, String label) {
        super(lReg, rReg, label);
    }
    
    @Override
    public String toString() {
        return super.toString("beq");
    }
}
