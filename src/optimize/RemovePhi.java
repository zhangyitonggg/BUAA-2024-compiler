package optimize;

import backend.Register.Reg;
import llvm.IrFactory;
import llvm.Module;
import llvm.value.Value;
import llvm.value.instr.*;
import llvm.value.notInstr.BasicBlock;
import llvm.value.notInstr.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * todo 之后再写吧，有三个地方需要改；
 * 1. 实现removePhi
 * 2. 实现copy2mips
 * 3. 在function2mips中小改一下
 */
public class RemovePhi {
    public static void work(Module module) {
        for (Function function : module.getFunctions()) {
            // 得到所有最初的bbs
            ArrayList<BasicBlock> bbs = new ArrayList<>();
            for (BasicBlock bb : function.getBbs()) {
                bbs.add(bb);
            }
            // 得到value2reg
            HashMap<Value, Reg> value2reg = function.getValue2reg();
            // 逐一处理
            for (BasicBlock bb : bbs) {
                tackleOneBb(bb, value2reg);
            }
        }
    }
    
    private static void tackleOneBb(BasicBlock curBb, HashMap<Value, Reg> value2reg) {
        // 获取curBb的每一个前序块的copy指令,并更新...
        HashMap<BasicBlock, ArrayList<Copy>> front2copy = getFront2copy(curBb);
        
        for (BasicBlock frontBb : curBb.frontBbs) {
            ArrayList<Copy> copyList = front2copy.get(frontBb);
            // 如果frontBb中根本就没有copy，那直接跳过即可
            if (copyList.isEmpty()) {
                continue;
            }
            // 构造可以并行的copy指令序列
            ArrayList<Copy> fakePCopyList = getParallelCopyList(copyList, curBb);
            // 寄存器分配之后，可能会存在寄存器共用的问题
            ArrayList<Copy> pCopyList = tackleRegError(fakePCopyList, curBb, value2reg);
            // 开始将copy指令插入进去，对于有多个后继的块，需要构造一个中间块
            if (frontBb.backBbs.size() < 2) {
                for (Copy copy : pCopyList) {
                    LinkedList<Instruction> allInstr = frontBb.getAllInstr();
                    int index = allInstr.size() - 1;
                    allInstr.add(index, copy);
                }
            } else {
                // 有多个后继的情况
                // 生成一个bb并放到合适位置
                Function function = (Function) curBb.getHost();
                BasicBlock middleBb = new BasicBlock(function, IrFactory.nameCounter++);
                function.addBbBeforeOne(curBb, middleBb);
                // 向bb里面插入指令，包括copy和跳转指令
                for (Copy copy : pCopyList) {
                    middleBb.add2end(copy);
                }
                Jump jump = new Jump(middleBb, curBb);
                middleBb.add2end(jump);
                // 修改frontBb的最后一条branch指令
                Branch branch = (Branch) frontBb.getInstrAtEnd();
                branch.replaceOperand(curBb, middleBb); // Use关系应该就不准了，但是无所谓，反正以后也不用了
            }
        }
    }
    
    private static ArrayList<Copy> getParallelCopyList(ArrayList<Copy> inCopyList, BasicBlock curBb) {
        LinkedList<Copy> outCopyList =  new LinkedList<>();
        for (int i = 0; i < inCopyList.size(); i+=1) {
            Copy iCopy = inCopyList.get(i);
            for (int j = i + 1; j < inCopyList.size(); j+=1) {
                // i 目前在 j 前面
                Copy jCopy = inCopyList.get(j);
                if (iCopy.getTarget() != jCopy.getFrom()) {
                    // i 写入的东西不是 j 使用的东西，那当然没有问题
                    // 话说如果能用Verilog写就好了，，，
                    continue;
                }
                // 下面说明有问题，也就是i的target是j的from
                // 可以构造一个新的copy，将i写入一个新的middle value，然后将middle value写入j及之后需要的copy
                Value middleValue = IrFactory.makeMiddleValue(iCopy.getTarget().getType());
                Copy middleCopy = new Copy(curBb, middleValue, iCopy.getTarget());
                outCopyList.addFirst(middleCopy);
                for (int k = j; k < inCopyList.size(); k+=1) {
                    Copy kCopy = inCopyList.get(k);
                    if (kCopy.getFrom() == iCopy.getTarget()) {
                        kCopy.setFrom(middleValue);
                    }
                }
            }
            outCopyList.addLast(iCopy);
        }
        return new ArrayList<>(outCopyList);
    }
    
    private static ArrayList<Copy> tackleRegError(ArrayList<Copy> fakePCopyList, BasicBlock curBb, HashMap<Value, Reg> value2reg) {
        ArrayList<Copy> pCopyList = new ArrayList<>();
        for (int i = 0; i < fakePCopyList.size(); i+=1) {
            Copy iCopy = fakePCopyList.get(i);
            for (int j = i + 1; j < fakePCopyList.size(); j+=1) {
                Copy jCopy = fakePCopyList.get(j);
                if (value2reg.containsKey(iCopy.getTarget()) && value2reg.containsKey(jCopy.getFrom()) &&
                        value2reg.get(iCopy.getTarget())  == value2reg.get(jCopy.getFrom())) {
                    
                    Value middleValue = IrFactory.makeMiddleValue(iCopy.getTarget().getType());
                    Copy middleCopy = new Copy(curBb, middleValue, iCopy.getTarget());
                    pCopyList.add(0, middleCopy);
                    for (int k = j; k < fakePCopyList.size(); k+=1) {
                        Copy kCopy = fakePCopyList.get(k);
                        if (value2reg.containsKey(kCopy.getFrom()) && value2reg.get(iCopy.getTarget())  == value2reg.get(kCopy.getFrom())) {
                            kCopy.setFrom(middleValue);
                        }
                    }
                }
            }
            pCopyList.add(iCopy);
        }
        return pCopyList;
    }
    
    
    private static HashMap<BasicBlock, ArrayList<Copy>> getFront2copy(BasicBlock curBb) {
        // 先初始化一波
        HashMap<BasicBlock, ArrayList<Copy>> front2copy = new HashMap<>();
        Iterator<Instruction> iterator = curBb.getAllInstr().iterator();
        for (BasicBlock frontBb : curBb.frontBbs) {
            front2copy.put(frontBb, new ArrayList<>());
        }
        // 开始干活
        while(iterator.hasNext()) {
            Instruction instruction = iterator.next();
            if (instruction instanceof Phi phi) {
                // 只有instruction是Phi指令的时候才需要干活
                // 首先先删除不可能到达的<value, frontBbs>对
                LinkedList<BasicBlock> fronts = phi.getBbs();
                ArrayList<Value> values = phi.getValues();
                ArrayList<Integer> toDeletes = new ArrayList<>();
                for (int i =  0; i < fronts.size(); i++) {
                    if (curBb.frontBbs.contains(fronts.get(i))) {
                        continue;
                    }
                    toDeletes.add(i);
                }
                for (int i = toDeletes.size() - 1; i >= 0; i--) {
                    fronts.remove(i);
                    values.remove(i);
                }
                // 之后更新front2copy
                for (int i = 0; i < values.size(); i+=1) {
                    Value value = values.get(i);
                    if (value.isJustPlaceholder()) {
                        continue;
                    }
                    BasicBlock front = fronts.get(i);
                    Copy copy = new Copy(front, phi, value);
                    front2copy.get(front).add(copy);
                }
                iterator.remove();
            }
        }
        return front2copy;
    }
}
