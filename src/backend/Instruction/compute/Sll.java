package backend.Instruction.compute;

import backend.InstrM;
import backend.Register.Reg;

public class Sll extends InstrM {
    protected Reg to;
    private Reg op1;
    private int imme;
    
    public Sll(Reg to, Reg op1, int imme) {
        this.to = to;
        this.op1 = op1;
        this.imme = imme;
    }
    
    @Override
    public String toString() {
        return "sll " + to.toString() + ", " + op1.toString() + ", " + imme;
    }
}
