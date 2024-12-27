package backend.Instruction.hilo;

import backend.InstrM;
import backend.Instruction.memory.Sb;
import backend.Register.Reg;

public abstract class HiLoM extends InstrM {
    Reg reg;
    
    public HiLoM(Reg reg) {
        this.reg = reg;
    }
    
    public String toString(String instr) {
        return instr + " " + reg.toString();
    }
}
