package optimize;

import backend.Register.Reg;
import llvm.Module;
import llvm.types.PointerIrTy;
import llvm.value.Value;
import llvm.value.instr.Alloca;
import llvm.value.instr.Call;
import llvm.value.instr.Instruction;
import llvm.value.instr.Phi;
import llvm.value.notInstr.BasicBlock;
import llvm.value.notInstr.Function;

import java.util.*;

// 采用一种退化的引用计数的方法
public class RegAlloca {
    private final Module module;
    private HashMap<Value, Double> value2citeNum;
    private HashMap<Reg, Value> reg2value;
    private HashMap<Value, Reg> value2reg;
    private ArrayList<Reg> freeRegs;
    
    private RegAlloca(Module module) {
        this.module = module;
    }
    private static RegAlloca object;
    public static RegAlloca getInstance(Module module) {
        if (object == null) {
            object = new RegAlloca(module);
        }
        return object;
    }
    
    public void alloca() {
        analyze(module);
        // 处理所有函数
        for (Function function : module.getFunctions()) {
            tackleFunction(function);
        }
    }
    
    private void tackleFunction(Function function) {
        // 初始化一波
        value2citeNum = new HashMap<>();
        reg2value = new HashMap<>();
        value2reg = new HashMap<>();
        freeRegs = getCanAllocaRegs();
        // 计算各个value的引用值
        ArrayList<Instruction> instructions = function.getAllInstr();
        int size = instructions.size();
        for (int i = 0; i < size; i++) {
            Instruction instruction = instructions.get(i);
            if (null != instruction.getName()) {
                if (value2citeNum.containsKey(instruction)) {
                    double tmp = value2citeNum.get(instruction);
                    tmp = tmp  + (1.0 * size - 1.0 * i) * i + 1;
                    value2citeNum.put(instruction, tmp);
                } else {
                    double tmp = (1.0 * size - 1.0 * i) * i + 1;
                    value2citeNum.put(instruction, tmp);
                }
            }
            for (Value v : instruction.getAllOperands()) {
                if (value2citeNum.containsKey(instruction)) {
                    double tmp = value2citeNum.get(instruction);
                    tmp = tmp  + (1.0 * size - 1.0 * i) * i + 1.2;
                    value2citeNum.put(instruction, tmp);
                } else {
                    double tmp = (1.0 * size - 1.0 * i) * i + 1.2;
                    value2citeNum.put(instruction, tmp);
                }
            }
        }
        // 分配寄存器
//        for (int i = 0; i < size; i++) {
//            Instruction instruction = instructions.get(i);
//            if (instruction.getName() != null && !(instruction instanceof Alloca && ((PointerIrTy) instruction.getType()).deRefIrTy.isArray())) {
//                if (freeRegs.isEmpty()) {
//                    Value toDie = null;
//                    for (Value value : value2reg.keySet()) {
//                        if (toDie == null) {
//                            toDie = value;
//                        } else {
//                            if (value2citeNum.get(value) < value2citeNum.get(toDie)) {
//                                toDie = value;
//                            }
//                        }
//                    }
//                    if (value2citeNum.get(instruction) > value2citeNum.get(toDie)) {
//                        Reg reg = value2reg.get(toDie);
//                        value2reg.remove(toDie);
//                        reg2value.remove(reg);
//                        value2reg.put(instruction, reg);
//                        reg2value.put(reg, instruction);
//                    }
//                } else {
//                    Reg reg = freeRegs.get(freeRegs.size() - 1);
//                    freeRegs.remove(freeRegs.size() - 1);
//                    value2reg.put(instruction, reg);
//                    reg2value.put(reg, instruction);
//                }
//            }
//        }
        // 设置寄存器分配
        BasicBlock entryBb = function.getFirstBb();
        allocaBlock(entryBb);
        for (BasicBlock bb : function.getBbs()) {
            LinkedList<Instruction> bbInstructions = bb.getAllInstr();
            for (int i = 0; i < bbInstructions.size(); i+=1) {
                Instruction instruction = bbInstructions.get(i);
                if (!(instruction instanceof Call)) {
                    continue;
                }
                HashSet<Reg> tmpRegs = new HashSet<>();
                for (Value v : bb.outSet) {
                    if (value2reg.containsKey(v)) {
                        Reg r = value2reg.get(v);
                        tmpRegs.add(r);
                    }
                }
                for (int j = i + 1; j < bbInstructions.size(); j+=1) {
                    Instruction jInstr = bbInstructions.get(j);
                    for (Value v : jInstr.getAllOperands()) {
                        if (value2reg.containsKey(v)) {
                            Reg r = value2reg.get(v);
                            tmpRegs.add(r);
                        }
                    }
                }
                ((Call) instruction).liveRegSet = tmpRegs;
            }
        }
        
        function.setValue2reg(value2reg);
    }
    
    private ArrayList<Reg> getCanAllocaRegs() {
        return new ArrayList<>(){{
            add(Reg.v1); add(Reg.gp); add(Reg.fp);
            add(Reg.t0); add(Reg.t1); add(Reg.t2); add(Reg.t3); add(Reg.t4); add(Reg.t5); add(Reg.t6); add(Reg.t7); add(Reg.t8); add(Reg.t9);
            add(Reg.s0); add(Reg.s1); add(Reg.s2); add(Reg.s3); add(Reg.s4); add(Reg.s5); add(Reg.s6); add(Reg.s7);
        }};
    }
    
    private void allocaBlock(BasicBlock curBb) {
        HashSet<Value> noUsedSet = new HashSet<>();
        HashMap<Value, Instruction> value2finalUse = new HashMap<>();
        HashSet<Value> hasDefSet = new HashSet<>();
        LinkedList<Instruction> instructions = curBb.getAllInstr();
        
        for (Instruction instruction : instructions) {
            for (Value operand : instruction.getAllOperands()) {
                value2finalUse.put(operand, instruction);
            }
        }
        
        for (Instruction instruction : instructions) {
            if (!(instruction instanceof Phi)) {
                for (Value value : instruction.getAllOperands()) {
                    if (value2finalUse.get(value) == instruction) {
                        if (value2reg.containsKey(value) && curBb.outSet.contains(value) == false) {
                            Reg reg = value2reg.get(value);
                            noUsedSet.add(value);
                            reg2value.remove(reg);
                        }
                    }
                }
            }
            
            if (instruction.getName() == null) {
                continue;
            }
            if (instruction instanceof Alloca alloca && ((PointerIrTy) alloca.getType()).deRefIrTy.isArray()) {
                continue;
            }
            hasDefSet.add(instruction);
            // 下面尝试分配寄存器
            Reg res = null;
            for (Reg reg : freeRegs) {
                if (reg2value.containsKey(reg)) {
                    continue;
                }
                res = reg;
                break;
            }
            if (res != null) {
                reg2value.put(res, instruction);
                value2reg.put(instruction, res);
            } else {
                Double min = Double.MAX_VALUE;
                for (Reg tmpReg : freeRegs) {
                    Value tmpValue = reg2value.get(tmpReg);
                    Double tmpDouble = value2citeNum.get(tmpValue);
                    if (tmpDouble < min) {
                        res = tmpReg;
                        min = tmpDouble;
                    }
                }
                if (value2citeNum.get(instruction) > min) {
                    if (reg2value.containsKey(res)) {
                        Value resValue = reg2value.get(res);
                        value2reg.remove(resValue);
                    }
                    reg2value.put(res, instruction);
                    value2reg.put(instruction, res);
                }
            }
        }
        
        for (BasicBlock bb : curBb.immeDomTos) {
            HashMap<Reg, Value> afterNoUse = new HashMap<>();
            for (Reg reg : reg2value.keySet()) {
                Value v = reg2value.get(reg);
                if (bb.inSet.contains(v)) {
                    continue;
                }
                afterNoUse.put(reg, v);
            }
            for (Reg reg : afterNoUse.keySet()) {
                reg2value.remove(reg);
            }
            // dfs
            allocaBlock(bb);
            for (Reg reg : afterNoUse.keySet()) {
                Value v = afterNoUse.get(reg);
                reg2value.put(reg, v);
            }
        }
        
        for (Value v : hasDefSet) {
            if (value2reg.containsKey(v)) {
                Reg r = value2reg.get(v);
                reg2value.remove(r);
            }
        }
        
        for (Value v : noUsedSet) {
            if (hasDefSet.contains(v)) {
                continue;
            }
            if (!value2reg.containsKey(v)) {
                continue;
            }
            Reg r = value2reg.get(v);
            reg2value.put(r, v);
        }
    }
    
    ///////////////////////////////////////////////////////////////////
    private void analyze(Module module) {
        for (Function function : module.getFunctions()) {
            HashMap<BasicBlock, HashSet<Value>> bb2In = new HashMap<>();
            HashMap<BasicBlock, HashSet<Value>> bb2Out = new HashMap<>();
            for (BasicBlock curBb : function.getBbs()) {
                curBb.makeDefUse();
                bb2Out.put(curBb, new HashSet<>());
                bb2In.put(curBb, new HashSet<>());
            }
            boolean needBreak = false;
            while(needBreak == false) {
                needBreak = true;
                LinkedList<BasicBlock> bbs = function.getBbs();
                for (int i = bbs.size() - 1; i >= 0; i-=1) {
                    BasicBlock curBb = bbs.get(i);
                    HashSet<Value> outSet = new HashSet<>();
                    HashSet<Value> inSet = new HashSet<>();
                    for (BasicBlock back : curBb.backBbs) {
                        HashSet<Value> backInSet = bb2In.get(back);
                        for (Value v : backInSet) {
                            outSet.add(v);
                        }
                    }
                    for (Value v : outSet) {
                        inSet.add(v);
                    }
                    for (Value v : curBb.defSet) {
                        if (inSet.contains(v)) {
                            inSet.remove(v);
                        }
                    }
                    for (Value v : curBb.useSet) {
                        if (!inSet.contains(v)) {
                            inSet.add(v);
                        }
                    }
                    if (!outSet.equals(bb2Out.get(curBb)) || !inSet.equals(bb2In.get(curBb))) {
                        needBreak = false;
                    }
                    bb2Out.put(curBb, outSet);
                    bb2In.put(curBb, inSet);
                }
                for (BasicBlock curBb : function.getBbs()) {
                    curBb.outSet = bb2Out.get(curBb);
                    curBb.inSet = bb2In.get(curBb);
                }
            }
            
        }
    }
}
