package backend.Instruction.compare;

import backend.InstrM;
import backend.Register.Reg;

/**
 * xxx to, op1, op2
 * op2只能是寄存器，不能是立即数
 */
public abstract class CompareM extends InstrM {
    protected Reg to;
    
    protected Reg op1;
    
    protected Reg op2;
    
    public CompareM(Reg to, Reg op1, Reg op2) {
        this.to = to;
        this.op1 = op1;
        this.op2 = op2;
    }
    
    public String toString(String instr) {
        StringBuilder sb = new StringBuilder();
        sb.append(instr);
        sb.append(" ");
        sb.append(to.toString());
        sb.append(", ");
        sb.append(op1.toString());
        sb.append(", ");
        sb.append(op2.toString());
        return sb.toString();
    }
}
