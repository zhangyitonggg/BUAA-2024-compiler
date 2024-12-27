package backend.Instruction.assign;

import backend.Register.Reg;

/**
 * li $1, imme16
 * 伪指令：
 * li $1, imme32  这个伪指令展开后只需要本寄存器
 */
public class Li extends AssignM {
    public Li(Reg to, int from) {
        super(to, (Integer) from);
    }
    
    public int getFrom() {
        return (Integer) this.from;
    }
    
    @Override
    public String toString() {
        return "li " + to.toString() + ", " + getFrom();
    }
}
