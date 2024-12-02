package backend.Instruction.memory;

import backend.Register.Reg;

public class Sw extends MemoryM {
    public Sw(Reg src, int imme, Reg base) {
        super(src, imme, base);
    }
    
    @Override
    public String toString() {
        return super.toString("sw");
    }
}
