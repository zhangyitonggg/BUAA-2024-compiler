package backend.Instruction.hilo;

import backend.Register.Reg;

public class Mfhi extends HiLoM{
    public Mfhi(Reg src) {
        super(src);
    }
    
    @Override
    public String toString() {
        return super.toString("mfhi") ;
    }
}
