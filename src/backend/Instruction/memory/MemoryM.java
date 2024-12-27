package backend.Instruction.memory;

import backend.InstrM;
import backend.Register.Reg;

/**
 * memory reg,imme(base)
 */
public abstract class MemoryM extends InstrM {
    protected Reg reg;
    protected int imme;
    protected Reg base;
    
    protected MemoryM(Reg reg, int imme, Reg base) {
        this.reg = reg;
        this.imme = imme;
        this.base = base;
    }

    public String toString(String instr) {
        StringBuilder sb = new StringBuilder();
        sb.append(instr).append(" ");
        sb.append(reg.toString()).append(", ");
        sb.append(imme).append("(");
        sb.append(base.toString()).append(")");
        return sb.toString();
    }
}
