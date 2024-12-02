package backend.Instruction.memory;

import backend.Register.Reg;

public class Lb extends MemoryM {
    public Lb(Reg dst, int imme, Reg base) {
        super(dst, imme, base);
    }
    
    @Override
    public String toString() {
        return super.toString("lb");
    }
}
