package backend.Instruction.branch;

import backend.Register.Reg;

public class Ble extends BranchM {
    public Ble(Reg lReg, Reg rReg, String label) {
        super(lReg, rReg, label);
    }

    @Override
    public String toString() {
        return super.toString("ble");
    }
}
