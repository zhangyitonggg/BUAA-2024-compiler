package optimize;

import backend.Register.Reg;
import llvm.Module;
import llvm.types.PointerIrTy;
import llvm.value.Value;
import llvm.value.instr.Alloca;
import llvm.value.instr.Instruction;
import llvm.value.notInstr.BasicBlock;
import llvm.value.notInstr.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        for (int i = 0; i < size; i++) {
            Instruction instruction = instructions.get(i);
            if (instruction.getName() != null && !(instruction instanceof Alloca && ((PointerIrTy) instruction.getType()).deRefIrTy.isArray())) {
                if (freeRegs.isEmpty()) {
                    Value toDie = null;
                    for (Value value : value2reg.keySet()) {
                        if (toDie == null) {
                            toDie = value;
                        } else {
                            if (value2citeNum.get(value) < value2citeNum.get(toDie)) {
                                toDie = value;
                            }
                        }
                    }
                    if (value2citeNum.get(instruction) > value2citeNum.get(toDie)) {
                        Reg reg = value2reg.get(toDie);
                        value2reg.remove(toDie);
                        reg2value.remove(reg);
                        value2reg.put(instruction, reg);
                        reg2value.put(reg, instruction);
                    }
                } else {
                    Reg reg = freeRegs.get(freeRegs.size() - 1);
                    freeRegs.remove(freeRegs.size() - 1);
                    value2reg.put(instruction, reg);
                    reg2value.put(reg, instruction);
                }
            }
        }
        // 设置寄存器分配
        function.setValue2reg(value2reg);
    }
    
    private ArrayList<Reg> getCanAllocaRegs() {
        return new ArrayList<>(){{
            add(Reg.v1); add(Reg.gp); add(Reg.fp);
            add(Reg.t0); add(Reg.t1); add(Reg.t2); add(Reg.t3); add(Reg.t4); add(Reg.t5); add(Reg.t6); add(Reg.t7); add(Reg.t8); add(Reg.t9);
            add(Reg.s0); add(Reg.s1); add(Reg.s2); add(Reg.s3); add(Reg.s4); add(Reg.s5); add(Reg.s6); add(Reg.s7);
        }};
    }
    

}
