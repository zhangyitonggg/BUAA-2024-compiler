package backend.Instruction.branch;

import backend.Register.Reg;

public class Bgt extends BranchM {
    public Bgt(Reg lReg, Reg rReg, String label) {
        super(lReg, rReg, label);
    }

    @Override
    public String toString() {
        return super.toString("bgt");
    }
}
