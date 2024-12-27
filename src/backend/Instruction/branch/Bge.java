package backend.Instruction.branch;

import backend.Register.Reg;

public class Bge extends BranchM {
    public Bge(Reg lReg, Reg rReg, String label) {
        super(lReg, rReg, label);
    }

    @Override
    public String toString() {
        return super.toString("bge");
    }
}
