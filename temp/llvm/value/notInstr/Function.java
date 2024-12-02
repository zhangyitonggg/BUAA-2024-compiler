package llvm.value.notInstr;

import backend.Register.Reg;
import llvm.types.*;
import llvm.value.Value;
import llvm.value.instr.Branch;
import llvm.value.instr.Instruction;
import llvm.value.instr.Jump;
import llvm.value.notInstr.BasicBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Function extends Value {
    private final LinkedList<BasicBlock> bbs = new LinkedList<>();
    private final ArrayList<FParam> fParams = new ArrayList<>();
    private HashMap<Value, Reg> value2reg = new HashMap<>();
    
    public Function(String name, DataIrTy returnTy, ArrayList<DataIrTy> fParamTys) {
        super(null, "@" + name, new FuncIrTy(returnTy, fParamTys));
        if (fParamTys == null) {
            return;
        }
        for (int i = 0; i < fParamTys.size(); i++) {
            fParams.add(new FParam(this, i, fParamTys.get(i)));
        }
    }
    
    public Function(String name, DataIrTy returnTy) {
        super(null, "@" + name, new FuncIrTy(returnTy, new ArrayList<>()));
    }
    
    public ArrayList<Instruction> getAllInstr() {
        ArrayList<Instruction> res = new ArrayList<>();
        for (BasicBlock bb : bbs) {
            res.addAll(bb.getAllInstr());
        }
        return res;
    }
    
    public DataIrTy getReturnType() {
        return ((FuncIrTy) getType()).returnTy;
    }
    
    public ArrayList<DataIrTy> getFParamTys() {
        return ((FuncIrTy) getType()).fParamTys;
    }
   
    public ArrayList<FParam> getFParams() {
        return fParams;
    }
    
    public BasicBlock getFirstBb() {
        return bbs.get(0);
    }
    
    public LinkedList<BasicBlock> getBbs() {
        return bbs;
    }
    
    public void addBb2end(BasicBlock basicBlock) {
        bbs.addLast(basicBlock);
    }
    
    public void addBbBeforeOne(BasicBlock one, BasicBlock targetBb) {
        int i;
        for (i = 0; i < bbs.size(); i++) {
            if (bbs.get(i) == one) {
                break;
            }
        }
        if (i >= 0) {
            bbs.add(i, targetBb);
        }
    }
    
    public void setValue2reg(HashMap<Value, Reg> value2reg) {
        this.value2reg = value2reg;
    }
    
    public HashMap<Value, Reg> getValue2reg() {
        return value2reg;
    }
    
    /**
     * 这里只考虑自定义的函数，对于库函数的Declare在Module的toString中进行
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ");
        sb.append(getReturnType());
        sb.append(" ");
        sb.append(getName());
        sb.append("(");
        for (int i = 0; i < fParams.size(); i++) {
            FParam fParam = fParams.get(i);
            sb.append(fParam.getType());
            sb.append(" ");
            sb.append(fParam.getName());
            if (i < fParams.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(") ");
        sb.append("{").append("\n");
        for (BasicBlock bb : bbs) {
            sb.append(bb.toString());
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * declare i32 @getint()
     * declare i32 @getchar()
     * declare void @putint(i32)
     * declare void @putch(i32)
     * declare void @putstr(i8*)
     */
    public final static Function getint = new Function("getint", DataIrTy.I32); // 返回的是I32
    public final static Function getchar = new Function("getchar", DataIrTy.I32);
    public final static Function putint = new Function("putint", new VoidIrTy(), new ArrayList<>(){
        {
            add(DataIrTy.I32);
        }
    });
    public final static Function putch = new Function("putch", new VoidIrTy(), new ArrayList<>(){
        {
            add(DataIrTy.I32);
        }
    });
    public final static Function putstr = new Function("putstr", new VoidIrTy(), new ArrayList<>(){
        {
            add(new PointerIrTy(DataIrTy.I8));
        }
    });
    
    /////////////////////////////////////////////////////////////////////////////
    public void calcCFG() {
        for (BasicBlock bb : bbs) {
            bb.isVisited = false;
            bb.frontBbs = new HashSet<>();
            bb.backBbs = new HashSet<>();
        }
        makeFrontAndBack(getFirstBb());
    }
    
    public void calcDomByRel() {
        initDomBys();
        boolean calcing = true;
        while(calcing) {
            calcing = false;
            // 跳过entry
            for (int i = 1; i < bbs.size(); i++) {
                BasicBlock bb = bbs.get(i);
                HashSet<BasicBlock> ansBbs = new HashSet<>(){{
                    addAll(bbs);
                }};
                // 遍历每一个先驱
                for (BasicBlock frontBB : bb.frontBbs) {
                    // 求二者domBys交集
                    HashSet<BasicBlock> tempBbs = new HashSet<>();
                    for (BasicBlock tmp : ansBbs) {
                        if (frontBB.domBys.contains(tmp)) {
                            tempBbs.add(tmp);
                        }
                    }
                    ansBbs = tempBbs;
                }
                // 还需要加上自身
                ansBbs.add(bb);
                // 判断ansBbs是否和bb的domBys相同
                if (ansBbs.size() != bb.domBys.size()) {
                    calcing = true;
                } else {
                    boolean flag = false;
                    for (BasicBlock tmp : bb.domBys) {
                        if (!ansBbs.contains(tmp)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        calcing = true;
                    }
                }
                bb.domBys = ansBbs;
            }
        }
    }
    
    private void initDomBys() {
        // entry块的domBys仅有自身
        bbs.get(0).domBys = new HashSet<>(){{
            add(bbs.get(0));
        }};
        // 先初始化除entry外的domBys块为全部bb
        for (int i = 1; i < bbs.size(); i++) {
            bbs.get(i).domBys = new HashSet<>(){{
                addAll(bbs);
            }};
        }
    }
    
    private void makeFrontAndBack(BasicBlock curBb) {
        if (curBb.isVisited) {
            return;
        }
        curBb.isVisited = true;
        Instruction instruction = curBb.getInstrAtEnd();
        if (instruction instanceof Jump jump) {
            BasicBlock targetBb = (BasicBlock) jump.getOperand(0);
            curBb.backBbs.add(targetBb);
            targetBb.frontBbs.add(curBb);
            makeFrontAndBack(targetBb);
        } else if (instruction instanceof Branch branch) {
            BasicBlock bb1 = (BasicBlock) branch.getOperand(1);
            BasicBlock bb2 = (BasicBlock)  branch.getOperand(2);
            
            curBb.backBbs.add(bb1);
            bb1.frontBbs.add(curBb);
            makeFrontAndBack(bb1);
            
            curBb.backBbs.add(bb2);
            bb2.frontBbs.add(curBb);
            makeFrontAndBack(bb2);
        }
    }
    
    // 计算直接支配者
    public void calcImmeDom() {
        for (BasicBlock curBb : bbs) {
            // 越过entry，因为entry没有imme
            if (curBb == bbs.get(0)) {
                continue;
            }
            // 遍历所有的支配者
            for (BasicBlock domBy : curBb.domBys) {
                // 因为自己也是自己的支配者，应该略过这种情况
                if (domBy == curBb) {
                    continue;
                }
                boolean flag = true; // 说明是直接支配者
                for (BasicBlock otherDomBy : curBb.domBys) {
                    if (otherDomBy == curBb) {
                        continue;
                    }
                    if (otherDomBy == domBy) {
                        continue;
                    }
                    if (otherDomBy.domBys.contains(domBy)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    domBy.immeDomTos.add(curBb);
                    curBb.immeDomBy = domBy;
                    break;
                }
            }
        }
    }
    
    // 计算支配边界
    public void calcDomFro() {
        // 目前先不需要清空原有的df
        for (BasicBlock curBb : bbs) {
            for (BasicBlock backBb : curBb.backBbs) {
                BasicBlock tmp = curBb;
                while(tmp == backBb || !backBb.domBys.contains(tmp)) {
                    tmp.domFro.add(backBb);
                    tmp = tmp.immeDomBy;
                }
            }
        }
    }
    
    public void deleteBb(int i) {
        BasicBlock bb = bbs.get(i);
        for (Instruction instruction : bb.getAllInstr()) {
            instruction.clearAllOperands();
        }
        bb.setLive2False();
        bbs.remove(i);
    }
}