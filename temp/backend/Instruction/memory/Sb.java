package backend.Instruction.memory;

import backend.Register.Reg;

public class Sb extends MemoryM {
    public Sb(Reg src, int imme, Reg base) {
        super(src, imme, base);
    }
    
    @Override
    public String toString() {
        return super.toString("sb");
    }
}
