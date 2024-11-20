package backend.Instruction.branch;

import backend.Register.Reg;

public class Blt extends BranchM {
    public Blt(Reg lReg, Reg rReg, String label) {
        super(lReg, rReg, label);
    }

    @Override
    public String toString() {
        return super.toString("blt");
    }
}
