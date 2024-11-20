package backend.Instruction.hilo;

import backend.Register.Reg;

public class Mthi extends HiLoM{
    public Mthi(Reg dst) {
        super(dst);
    }
    
    @Override
    public String toString() {
        return super.toString("mthi") ;
    }
}
