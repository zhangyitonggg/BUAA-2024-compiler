package backend.Instruction.compute;


import backend.InstrM;
import backend.Register.Reg;

/**
 * 主要分两类：
 * 有三操作数的：
 * addu
 * subu
 * sll(待实现)
 * sra(待实现)
 * srl(待实现)
 * 有两操作数的：
 * mult
 * div
 */
public class ComputeM extends InstrM {
    /**
     * to属性对于两操作数的指令而言，是为null的
     */
    protected Reg to;
    protected Reg op1;
    protected Reg op2;
    
    public ComputeM(Reg to, Reg op1, Reg op2) {
        this.to = to;
        this.op1 = op1;
        this.op2 = op2;
    }
    
    public boolean hasTo() {
        return to != null;
    }
    
    public String toString(String instr) {
        if (hasTo()) {
            return instr + " " + to + ", " + op1 + ", " + op2;
        } else {
            return instr + " " + op1 + ", " + op2;
        }
    }
}
