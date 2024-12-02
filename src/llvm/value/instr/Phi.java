package llvm.value.instr;

import llvm.types.DataIrTy;
import llvm.value.Value;
import llvm.value.notInstr.BasicBlock;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * phi指令只能出现在基本块的开头
 * <result> = phi [fast-math-flags] <ty> [<val0>, <label0>], ...
 */
public class Phi extends Instruction {
    private LinkedList<BasicBlock> bbs = new LinkedList<>();
    public Phi(BasicBlock host, int nameCount, DataIrTy dataIrTy, ArrayList<BasicBlock> blocks) {
        super(host, "%p" + nameCount, dataIrTy);
        for (BasicBlock block : blocks) {
            bbs.add(block);
            addOperand(null);
            block.addUse(this);
        }
    }
    
    public LinkedList<BasicBlock> getBbs() {
        return bbs;
    }
    
    public ArrayList<Value> getValues() {
        return getAllOperands();
    }
    
    public void fill(Value value, BasicBlock bb) {
        ArrayList<Value> all = getAllOperands();
        for (int i = 0; i < bbs.size(); i++) {
            if (bbs.get(i) == bb) {
                all.set(i, value);
            }
        }
        if (value != null) {
            value.addUse(this);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(" = phi ");
        sb.append(getType());
        sb.append(" ");
        for (int i = 0; i < bbs.size(); i++) {
            Value value = getOperand(i);
            BasicBlock bb = bbs.get(i);
            sb.append("[ ");
            sb.append(value.getName());
            sb.append(", ");
            sb.append(bb.getName());
            sb.append(" ]");
            if (i < bbs.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
