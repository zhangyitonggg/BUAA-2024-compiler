package llvm.value;

import llvm.types.IrTy;

import java.util.ArrayList;

public class User extends Value {
    private ArrayList<Value> operands; // 这些操作数均是this的used
    
    public User(Value host, String name, IrTy type, ArrayList<Value> operands) {
        super(host, name, type);
        this.operands = operands;
        // 更新used的信息
        for (Value operand : operands) {
            if (operand == null) {
                continue;
            }
            operand.addUse(this);
        }
    }
    public User(Value host, String name, IrTy type) {
        super(host, name, type);
        this.operands = new ArrayList<>();
    }
    
    public User(Value host, IrTy type) {
        super(host, type);
        this.operands = new ArrayList<>();
    }
    
    protected void setOperands(ArrayList<Value> operands) {
        this.operands = operands;
    }
    
    public void addOperand(Value operand) {
        operands.add(operand);
        operand.addUse(this);
    }

    public Value getOperand(int index) {
        if (index >= operands.size()) {
            return null;
        }
        return operands.get(index);
    }
    
    public ArrayList<Value> getAllOperands() {
        return this.operands;
    }
}
