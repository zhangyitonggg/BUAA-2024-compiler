package llvm.value.instr;

import llvm.types.ArrayIrTy;
import llvm.types.DataIrTy;
import llvm.value.Value;

import java.util.ArrayList;

public class Icmp extends Instruction {
    public enum Op {
        EQ,  // ==
        NE,  // !=
        SLT, // <
        SLE, // <=
        SGT, // >
        SGE, // >=
    }
    
    private final Op op;
    
    /**
     * 突然发现icmp比较的类型可以是I8和数字
     * @param op
     * @param nameCount
     * @param leftOperand
     * @param rightOperand
     */
    public Icmp(Value host, Op op, int nameCount, Value leftOperand, Value rightOperand) {
        super(host, "%v" + nameCount, DataIrTy.I1);
        this.op = op;
        
        ArrayList<Value> operands = new ArrayList<>();
        operands.add(leftOperand);
        operands.add(rightOperand);
        setOperands(operands);
    }
    
    public Op getOp() {
        return op;
    }
    
    @Override
    public String toString() {
        return getName() + " = icmp " + op.toString().toLowerCase() + " i32 " +
                getOperand(0).getName() + ", " + getOperand(1).getName();
    }
}
