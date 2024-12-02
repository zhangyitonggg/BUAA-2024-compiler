package backend.Instruction.compare;

import backend.Register.Reg;

/**
 *  ==
 */
public class Sne extends CompareM {
    public Sne(Reg to, Reg op1, Reg op2) {
        super(to, op1, op2);
    }
    
    @Override
    public String toString() {
        return super.toString("sne");
    }
}
