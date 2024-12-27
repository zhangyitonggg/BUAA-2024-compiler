package backend;

import backend.Instruction.SyscallM;
import backend.Instruction.assign.La;
import backend.Instruction.assign.Li;
import backend.Instruction.assign.Move;
import backend.Instruction.branch.Beq;
import backend.Instruction.compare.*;
import backend.Instruction.compute.*;
import backend.Instruction.hilo.Mfhi;
import backend.Instruction.hilo.Mflo;
import backend.Instruction.jump.J;
import backend.Instruction.jump.Jal;
import backend.Instruction.jump.Jr;
import backend.Instruction.memory.*;
import backend.Register.Reg;
import backend.data.*;
import llvm.value.Value;
import llvm.value.instr.Branch;
import llvm.value.instr.Compute;
import llvm.value.instr.Icmp;
import llvm.value.instr.Store;
import llvm.value.notInstr.Function;

import java.util.ArrayList;
import java.util.HashMap;

public class MipsFactory {
    // .data段
    // 在 .data 段汇编器是会自动完成字节对齐的操作的
    private final ArrayList<WordData> wordDataList = new ArrayList<>();
    private final ArrayList<ByteData> byteDataList = new ArrayList<>();
    private final ArrayList<SpaceData> spaceDataList = new ArrayList<>();
    private final ArrayList<AsciiData> asciiDataList = new ArrayList<>();
    
    // .text段
    // 这里全都是instruction
    protected final ArrayList<InstrM> instrList = new ArrayList<>();
    
    
    protected int curOffset = 0;
    protected HashMap<Value, Integer> value2offset = new HashMap<>();
    protected HashMap<Value, Reg> value2reg = new HashMap<>();
    protected Function curFunc;
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected void makeAsciiData(String name, String literal) {
        AsciiData asciiData = new AsciiData(name, literal);
        asciiDataList.add(asciiData);
    }
    
    protected void makeSpaceData(String name, int byteNum) {
        SpaceData spaceData = new SpaceData(name, byteNum);
        spaceDataList.add(spaceData);
    }
    
    protected void makeWordData(String name, ArrayList<Integer> inits) {
        WordData wordData = new WordData(name , inits);
        wordDataList.add(wordData);
    }
    
    protected void makeByteData(String name, ArrayList<Integer> inits) {
        ByteData byteData = new ByteData(name, inits);
        byteDataList.add(byteData);
    }
    
    protected void beginFunction(Function function) {
        curFunc = function;
        curOffset = 0;
        value2offset = new HashMap<>();
        // value2reg = new HashMap<>();
        value2reg = function.getValue2reg();
//        System.out.println(function.getName());
//        for (Value value : value2reg.keySet()) {
//            System.out.print(value + "  :  ");
//            System.out.println(value2reg.get(value));
//        }
//        System.out.println("-------------------------------------------------------------------------------------------");
        Label label = new Label(function.getName().substring(1));
        instrList.add(label);
    }
    
    protected void makeLabel(String name) {
        Label label = new Label(name);
        instrList.add(label);
    }
    
    protected void makeCompute(Compute compute, Reg target, Reg op1, Reg op2) {
        int index = instrList.size();
        
        Compute.Op op = compute.getOp();
        makeCompute(op, target, op1, op2);
        
        // 回填注释
        Note note = new Note(compute);
        InstrM instrM = instrList.get(index);
        instrM.setNote(note);
    }
    
    protected void makeCompute(Compute.Op op, Reg target, Reg op1, Reg op2) {
        if (op == Compute.Op.ADD) {
            Addu addu = new Addu(target, op1, op2);
            instrList.add(addu);
        } else if (op == Compute.Op.SUB) {
            Subu subu = new Subu(target, op1, op2);
            instrList.add(subu);
        } else if (op == Compute.Op.MUL) {
            Mult mult = new Mult(op1, op2);
            instrList.add(mult);
            Mflo mflo = new Mflo(target);
            instrList.add(mflo);
        } else if (op == Compute.Op.SDIV) {
            Div div = new Div(op1, op2);
            instrList.add(div);
            Mflo mflo = new Mflo(target);
            instrList.add(mflo);
        } else if (op == Compute.Op.SREM){ // SREM
            Div div = new Div(op1, op2);
            instrList.add(div);
            Mfhi mfhi = new Mfhi(target);
            instrList.add(mfhi);
        }
    }
    
    protected void makeSll(Reg target, Reg op1, int imme) {
        Sll sll = new Sll(target, op1, imme);
        instrList.add(sll);
    }
    
    protected Li makeLi(Reg target, int imme) {
        Li li = new Li(target, imme);
        instrList.add(li);
        return li;
    }
    
    protected void makeSyscall() {
        SyscallM syscall = new SyscallM();
        instrList.add(syscall);
    }
    
    protected Move makeMove(Reg to, Reg from) {
        Move move = new Move(to, from);
        instrList.add(move);
        return move;
    }
    
    protected MemoryM makeStore(int align, Reg src, int imme, Reg base) {

        
        if (align == 1) {
            Sb sb = new Sb(src, imme, base);
            instrList.add(sb);
            
            if (src == Reg.v1 && imme == -24) {
                for (Value v : value2reg.keySet()) {
                    if (Reg.v1 == value2reg.get(v)) {
                        System.out.print(align + "  ");
                        System.out.println(v);
                    }
                }
                System.out.println(instrList.get(instrList.size()-3));
            }
            return sb;
        } else {
            Sw sw = new Sw(src, imme, base);
            instrList.add(sw);
            return sw;
        }
    }
    
    protected MemoryM makeLoad(int align, Reg target, int imme, Reg base) {
        if (align == 1) {
            Lb lb = new Lb(target, imme, base);
            instrList.add(lb);
            return lb;
        } else {
            Lw lw = new Lw(target, imme, base);
            instrList.add(lw);
            return lw;
        }
    }
    
    protected La makeLa(Reg target, String name) {
        La la = new La(target, name);
        instrList.add(la);
        return la;
    }
    
    protected Beq makeBeq(Reg lReg, Reg rReg, String label) {
        Beq beq = new Beq(lReg, rReg, label);
        instrList.add(beq);
        return beq;
    }
    
    protected J makeJ(String label) {
        J j = new J(label);
        instrList.add(j);
        return j;
    }
    
    protected Jal makeJal(String label) {
        Jal jal = new Jal(label);
        instrList.add(jal);
        return jal;
    }
    
    protected Jr makeJr(Reg target) {
        Jr jr = new Jr(target);
        instrList.add(jr);
        return jr;
    }
    
    protected void makeCompare(Icmp.Op op, Reg to, Reg op1, Reg op2) {
        if (op.equals(Icmp.Op.EQ)) {
            Seq seq = new Seq(to, op1, op2);
            instrList.add(seq);
        } else if (op.equals(Icmp.Op.NE)) {
            Sne sne = new Sne(to, op1, op2);
            instrList.add(sne);
        } else if (op.equals(Icmp.Op.SLT)) {
            Slt slt = new Slt(to, op1, op2);
            instrList.add(slt);
        } else if (op.equals(Icmp.Op.SLE)) {
            Sle sle = new Sle(to, op1, op2);
            instrList.add(sle);
        } else if (op.equals(Icmp.Op.SGT)) {
            Sgt sgt = new Sgt(to, op1, op2);
            instrList.add(sgt);
        } else if (op.equals(Icmp.Op.SGE)) {
            Sge sge = new Sge(to, op1, op2);
            instrList.add(sge);
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public int subAlign(int delta, int align) {
        // 实际上目前align全是4
        // curOffset = curOffset - delta;
        // curOffset = LargestMultipleOfFour(curOffset);
        if (align == 1) {
            curOffset = curOffset - delta;
        } else {
            curOffset = curOffset - delta;
            curOffset = LargestMultipleOfFour(curOffset);
        }
        return curOffset;
    }
    
    public static int LargestMultipleOfFour(int number) {
        // 如果 number 本身是4的倍数，直接返回
        if (number % 4 == 0) {
            return number;
        }
        // 向下调整到最近的4的倍数
        if (number < 0) {
            return (number / 4) * 4 - 4;
        } else {
            return (number / 4) * 4;
        }
    }
    
    public void putOffset(Value value, int offset) {
        value2offset.put(value, offset);
    }
    
    public void putReg(Value value, Reg reg) {
        value2reg.put(value, reg);
    }
    
    // 没找到则返回null
    public Integer findOffset(Value value) {
        if (value2offset.containsKey(value)) {
            return value2offset.get(value);
        }
        return null;
    }
    
    // 没找到则返回null
    public Reg findReg(Value value) {
        if (value2reg.containsKey(value)) {
            return value2reg.get(value);
        }
        return null;
    }
    
//    public Value findValueWithReg(Reg reg) {
//        for (Value value : value2reg.keySet()) {
//            if (value2reg.get(value) == reg) {
//                return value;
//            }
//        }
//        return null;
//    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("# by zyt 22373337\n");
        sb.append(".data").append("\n");
        for (WordData wordData : wordDataList) {
            sb.append(wordData.getNote());
            sb.append(wordData.toString()).append("\n");
        }
        sb.append("\n");
        for (SpaceData spaceData : spaceDataList) {
            if (spaceData.getByteNum() % 4 == 0) {
                sb.append(spaceData.getNote());
                sb.append(spaceData.toString()).append("\n");
            }
        }
        for (SpaceData spaceData : spaceDataList) {
            if (spaceData.getByteNum() % 4 != 0) {
                sb.append(spaceData.getNote());
                sb.append(spaceData.toString()).append("\n");
            }
        }
        sb.append("\n");
        for (ByteData byteData : byteDataList) {
            sb.append(byteData.getNote());
            sb.append(byteData.toString()).append("\n");
        }
        sb.append("\n");
        for (AsciiData asciiData : asciiDataList) {
            sb.append(asciiData.getNote());
            sb.append(asciiData.toString()).append("\n");
        }
        sb.append("\n\n");
        sb.append(".text").append("\n");
        for (InstrM instrM : instrList) {
            sb.append(instrM.getNote());
            if (!(instrM instanceof Label)) {
                sb.append("    ");
            }
            sb.append(instrM.toString());
            sb.append("\n");
        }
        sb.append("\n\n# finished");
        return sb.toString();
    }
}
