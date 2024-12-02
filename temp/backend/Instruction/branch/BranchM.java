package backend.Instruction.branch;

import backend.InstrM;
import backend.Register.Reg;

/**
 * xxx $t1, $t2, label
 * 只能使用寄存器
 */
public abstract class BranchM extends InstrM {
    protected Reg lReg;
    protected Reg rReg;
    protected String label;
    
    public BranchM(Reg lReg, Reg rReg, String label) {
        this.lReg = lReg;
        this.rReg = rReg;
        this.label = label;
    }
    
    public String toString(String instr) {
        StringBuilder sb = new StringBuilder();
        sb.append(instr);
        sb.append(" ");
        sb.append(lReg.toString());
        sb.append(", ");
        sb.append(rReg.toString());
        sb.append(", ");
        sb.append(label);
        return sb.toString();
    }
}
