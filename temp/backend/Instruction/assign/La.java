package backend.Instruction.assign;

import backend.Register.Reg;

/**
 * a: .byte 1
 * array: .word 0,1,2,3,4,5,6,7,8,9
 * Str0: .asciiz "hello "
 *
 * la $1 a
 * la $2 array
 * la $3 Str0
 */
public class La extends AssignM {
    public La(Reg to, String from) {
        super(to, from);
    }
    
    public String getFrom() {
        return (String) this.from;
    }
    
    @Override
    public String toString() {
        return  "la " + to.toString() + ", " + (String) this.from;
    }
}
