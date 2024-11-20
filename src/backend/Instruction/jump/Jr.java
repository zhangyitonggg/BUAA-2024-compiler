package backend.Instruction.jump;

import backend.Register.Reg;

public class Jr extends JumpM {
    public Jr(Reg target) {
        super(target);
    }
    
    @Override
    public String toString() {
        return super.toString("jr");
    }
}
