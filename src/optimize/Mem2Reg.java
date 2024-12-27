package optimize;

import backend.Instruction.assign.La;
import llvm.IrFactory;
import llvm.Module;
import llvm.Use;
import llvm.types.DataIrTy;
import llvm.types.PointerIrTy;
import llvm.value.User;
import llvm.value.Value;
import llvm.value.constant.ConstData;
import llvm.value.instr.*;
import llvm.value.notInstr.BasicBlock;
import llvm.value.notInstr.Function;

import java.util.*;

public class Mem2Reg {
    private static Stack<Value> writeStack = new Stack<>();
    private static ArrayList<BasicBlock> readBbList = new ArrayList<>();
    private static ArrayList<BasicBlock> writeBbList = new ArrayList<>();
    private static ArrayList<Instruction> readInstrList = new ArrayList<>();
    private static ArrayList<Instruction> writeInstrList = new ArrayList<>();
    
    public static void work(Module module) {
        for (Function function : module.getFunctions()) {
            // 所有alloca都提前移到了firstBb，也就是entry
            BasicBlock entryBb = function.getFirstBb();
            ArrayList<Instruction> tempList = new ArrayList<>(entryBb.getAllInstr());
            for (Instruction instruction : tempList) {
                if (instruction instanceof Alloca alloca) {
                    if (((PointerIrTy) alloca.getType()).deRefIrTy.isBCI()) {
                        addPhi(entryBb, alloca);
                    }
                }
            }
        }
    }
    
    private static void addPhi(BasicBlock entryBb, Alloca alloca) {
        // 准备工作
        writeStack.clear();
        readBbList.clear();
        writeBbList.clear();
        readInstrList.clear();
        writeInstrList.clear();
        // 更新一波list
        for (Use use : alloca.getUses()) {
            User user = use.getUser();
            if (!((BasicBlock) user.getHost()).getIsLive() ) {
                continue;
            }
            if (user instanceof Store store) {
                if (!writeBbList.contains((BasicBlock) store.getHost())) {
                    writeBbList.add((BasicBlock) store.getHost());
                }
                writeInstrList.add(store);
            } else if (user instanceof Load load) {
                if (!readBbList.contains((BasicBlock) load.getHost())) {
                    readBbList.add((BasicBlock) load.getHost());
                }
                readInstrList.add(load);
            }
        }
        
        HashSet<BasicBlock> vis = new HashSet<>();
        LinkedList<BasicBlock> work = new LinkedList<>(writeBbList);
        // 开始插入phi
        while (work.size() != 0) {
            BasicBlock curBb = work.get(0);
            work.remove(0);
            for (BasicBlock dfBb : curBb.domFro) {
                if (!vis.contains(dfBb)) {
                    // 如果vis中没有dfBb， 将其加入进去
                    vis.add(dfBb);
                    // 如果dfBb不在writeBbList中，dfBb的支配边界可能未被探索
                    if (!writeBbList.contains(dfBb)) {
                        work.add(dfBb);
                    }
                    Phi phi = IrFactory.makePhi(((PointerIrTy) alloca.getType()).deRefIrTy, dfBb);
                    dfBb.add2head(phi);
                    readInstrList.add(phi);
                    writeInstrList.add(phi);
                }
            }
        }
        // 进行重新命名的工作
        renew(alloca, entryBb);
    }
    
    private static void renew(Alloca curAlloca, BasicBlock curBb) {
        int counter = 0;
        Iterator<Instruction> iter = curBb.getAllInstr().iterator();
        while(iter.hasNext()) {
            Instruction instruction = iter.next();
            if (instruction instanceof Phi && writeInstrList.contains(instruction)) {
                counter += 1;
                writeStack.push(instruction);
            } else if (instruction == curAlloca) {
                iter.remove();
            } else if (instruction instanceof Store && writeInstrList.contains(instruction)) {
                counter += 1;
                // 将store所使用的值压栈
                writeStack.push(instruction.getOperand(0));
                instruction.clearAllOperands();
                iter.remove();;
            } else if (instruction instanceof Load && readInstrList.contains(instruction)) {
                Value top = writeStack.empty() ? new ConstData(DataIrTy.I32, 0, true) : writeStack.peek();
                instruction.giveUserNewUsed(top); // 感觉没必要加一个undefined
                instruction.clearAllOperands();
                iter.remove();
            }
        }
        // 遍历curBb的后继，将最新的write 或者说 define 写进phi指令中
        for (BasicBlock back : curBb.backBbs) {
            Instruction instruction = back.getAllInstr().get(0);
            if (readInstrList.contains(instruction) && instruction instanceof Phi phi) {
                Value top = writeStack.empty() ? new ConstData(DataIrTy.I32, 0, true) : writeStack.peek();
                phi.fill(top, curBb);
            }
        }
        for (BasicBlock immeDomTo : curBb.immeDomTos) {
            renew(curAlloca, immeDomTo);
        }
        while (counter > 0) {
            counter--;
            writeStack.pop();
        }
    }
}
