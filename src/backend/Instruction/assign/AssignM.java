package backend.Instruction.assign;

import backend.InstrM;
import backend.Register.Reg;

/**
 * 直接给某一个寄存器赋值
 */
public abstract class AssignM extends InstrM {
    protected Object from;
    protected Reg to;
    
    protected AssignM(Reg to, Object from) {
        this.to = to;
        this.from = from;
    }
    
    public Reg getTo() {
        return this.to;
    }
}
