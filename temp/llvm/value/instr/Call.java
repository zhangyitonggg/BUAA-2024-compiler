package llvm.value.instr;

import llvm.value.Value;
import llvm.value.notInstr.Function;

import java.util.ArrayList;

/**
 * 根据函数有无返回值分为两种情况:
 * <result> = call [ret attrs] <ty> <name>(<...args>)
 * call [ret attrs] <ty> <name>(<...args>)
 */
public class Call extends Instruction {
    private final boolean hasReturnValue;
    
    /**
     * 有返回值的情况
     * @param nameCount
     * @param function
     * @param rParams
     */
    public Call(Value host, int nameCount, Function function, ArrayList<Value> rParams) {
        super(host, "%v" + nameCount, function.getReturnType());
        addOperand(function);
        if (!rParams.isEmpty()) {
            for (Value rParam : rParams) {
                addOperand(rParam);
            }
        }
        hasReturnValue = true;
    }
    
    public Call(Value host, Function function, ArrayList<Value> rParams) {
        super(host, function.getReturnType());
        addOperand(function);
        if (!rParams.isEmpty()) {
            for (Value rParam : rParams) {
                addOperand(rParam);
            }
        }
        hasReturnValue = false;
    }
    
    public Function getFunction() {
        return (Function) getOperand(0);
    }
    
    public boolean hasReturnValue() {
        return hasReturnValue;
    }
    
    public ArrayList<Value> getRParams() {
        ArrayList<Value> res = new ArrayList<>();
        ArrayList<Value> all = getAllOperands();
        for (int i = 1; i < all.size(); i++) {
            res.add(all.get(i));
        }
        return res;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (hasReturnValue) {
            sb.append(getName());
            sb.append(" = ");
        }
        sb.append("call ");
        Function function = (Function) getOperand(0);
        sb.append(function.getReturnType()).append(" ").append(function.getName()).append("(");
        ArrayList<Value> operands = getAllOperands();
        for (int i = 1; i < operands.size(); i++) {
            Value rParam = operands.get(i);
            sb.append(rParam.getType()).append(' ').append(" ").append(rParam.getName());
            if (i < operands.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
