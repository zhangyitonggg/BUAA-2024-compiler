package backend.Instruction.memory;

import backend.Register.Reg;

public class Lw extends MemoryM {
    public Lw(Reg dst, int imme, Reg base) {
        super(dst, imme, base);
    }
    
    @Override
    public String toString() {
        return super.toString("lw");
    }
}
