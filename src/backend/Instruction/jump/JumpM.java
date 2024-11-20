package backend.Instruction.jump;

import backend.InstrM;

/**
 * 格式如下：
 * jump label
 */
public abstract class JumpM extends InstrM {
    protected Object target;
    
    protected JumpM(Object target) {
        this.target = target;
    }
    
    public String toString(String instr) {
        return instr + " " + target.toString();
    }
}
