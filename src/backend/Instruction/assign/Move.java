package backend.Instruction.assign;

import backend.Register.Reg;

/**
 * 伪指令
 * move $t1. $t2
 * 其实就是addu $t2, $0, $t1
 */
public class Move extends AssignM {
    public Move(Reg to, Reg from) {
        super(to, from);
    }
    
    public Reg getFrom() {
        return (Reg) this.from;
    }
    
    @Override
    public String toString() {
        return "move " + to.toString() + ", " + from.toString();
    }
}
