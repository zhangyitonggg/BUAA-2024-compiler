package backend.Instruction.hilo;

import backend.Register.Reg;

public class Mflo extends HiLoM{
    public Mflo(Reg src) {
        super(src);
    }
    
    @Override
    public String toString() {
        return super.toString("mflo") ;
    }
}
