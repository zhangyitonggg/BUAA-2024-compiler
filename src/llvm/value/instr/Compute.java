package llvm.value.instr;

import llvm.types.DataIrTy;
import llvm.value.Value;

import java.util.ArrayList;

public class Compute extends Instruction {
    public enum Op {
        ADD,
        SUB,
        AND,
        OR,
        MUL,
        SDIV,
        SREM
    }
    
    private final Op op;
    
    public Compute(Op op, int nameCount, Value leftOperand, Value rightOperand) {
        super("%v" + nameCount, DataIrTy.I32); // 此处一定是I32
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
        return getName() + " = " + op.toString().toLowerCase() + " i32 " +
                    getOperand(0).getName() + ", " + getOperand(1).getName();
    }
}
