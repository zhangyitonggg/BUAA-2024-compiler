package llvm.value.notInstr;

import llvm.types.LabelIrTy;
import llvm.value.Value;
import llvm.value.instr.Instruction;

import java.util.LinkedList;

public class BasicBlock extends Value {
    private final LinkedList<Instruction> instructions = new LinkedList<>();
    
    public BasicBlock(int nameCount) {
        super("%b" + nameCount, new LabelIrTy());
    }
    
    public void add2end(Instruction instruction) {
        instructions.addLast(instruction);
    }
    
    public LinkedList<Instruction> getAllInstr() {
        return this.instructions;
    }
    
    public Instruction getInstrAtEnd() {
        if (instructions.isEmpty()) {
            return null;
        }
        return instructions.getLast();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName().substring(1));
        sb.append(":").append("\n");
        for (Instruction instr : instructions) {
            sb.append("    ");
            sb.append(instr.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
