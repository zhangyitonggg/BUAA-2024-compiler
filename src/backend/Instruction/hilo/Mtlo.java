package backend.Instruction.hilo;

import backend.Register.Reg;

public class Mtlo extends HiLoM{
    public Mtlo(Reg dst) {
        super(dst);
    }
    
    @Override
    public String toString() {
        return super.toString("mtlo") ;
    }
}
