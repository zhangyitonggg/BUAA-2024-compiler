package llvm.value.notInstr;

import llvm.types.LabelIrTy;
import llvm.value.Value;
import llvm.value.instr.Instruction;
import llvm.value.instr.Phi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class BasicBlock extends Value {
    public boolean isVisited = false;
    private boolean isLive = true;
    public HashSet<BasicBlock> frontBbs = new HashSet<>();
    public HashSet<BasicBlock> backBbs = new HashSet<>();
    
    public HashSet<BasicBlock> domFro = new HashSet<>();
    
    public HashSet<BasicBlock> domBys= new HashSet<>();
    public BasicBlock immeDomBy; // 有且仅有一个
    public HashSet<BasicBlock> immeDomTos = new HashSet<>();
    
    public HashSet<Value> defSet = new HashSet<>();
    public HashSet<Value> useSet = new HashSet<>();
    public HashSet<Value> outSet = new HashSet<>();
    public HashSet<Value> inSet = new HashSet<>();
    
    private final LinkedList<Instruction> instructions = new LinkedList<>();
    
    public BasicBlock(Value host, int nameCount) {
        super(host, "%b" + nameCount, new LabelIrTy());
    }
    
    public void add2end(Instruction instruction) {
        instructions.addLast(instruction);
    }
    
    public void add2head(Instruction instruction) {
        instructions.addFirst(instruction);
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
    
    public void setLive2False() {
        this.isLive = false;
    }
    
    public boolean getIsLive() {
        return isLive;
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
    
    public void makeDefUse() {
        defSet = new HashSet<>();
        useSet = new HashSet<>();
        for (Instruction instruction : instructions) {
            if (instruction instanceof Phi phi) {
                for (Value val : phi.getValues()) {
                    if (isIFG(val)) {
                        useSet.add(val);
                    }
                }
            }
        }
        for (Instruction instruction : instructions) {
            for (Value value : instruction.getAllOperands()) {
                if (!defSet.contains(value) && isIFG(value)) {
                    useSet.add(value);
                }
            }
            if (instruction.getName() != null && !useSet.contains(instruction)) {
                defSet.add(instruction);
            }
        }
    }
    
    private boolean isIFG(Value value) {
        return value instanceof Instruction || value instanceof FParam || value instanceof GlobalVar;
    }
}
