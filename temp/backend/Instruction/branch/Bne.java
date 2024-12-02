package backend.Instruction.branch;

import backend.Register.Reg;

public class Bne extends BranchM {
    public Bne(Reg lReg, Reg rReg, String label) {
        super(lReg, rReg, label);
    }

    @Override
    public String toString() {
        return super.toString("bne");
    }
}
