package backend.Instruction.compare;

import backend.Register.Reg;

/**
 *  ==
 */
public class Sgt extends CompareM {
    public Sgt(Reg to, Reg op1, Reg op2) {
        super(to, op1, op2);
    }
    
    @Override
    public String toString() {
        return super.toString("sgt");
    }
}
