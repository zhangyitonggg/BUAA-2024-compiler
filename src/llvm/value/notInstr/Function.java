package llvm.value.notInstr;

import llvm.types.*;
import llvm.value.Value;
import llvm.value.instr.Instruction;
import llvm.value.notInstr.BasicBlock;

import java.util.ArrayList;
import java.util.LinkedList;

public class Function extends Value {
    private final LinkedList<BasicBlock> bbs = new LinkedList<>();
    private final ArrayList<FParam> fParams = new ArrayList<>();
    
    public Function(String name, DataIrTy returnTy, ArrayList<DataIrTy> fParamTys) {
        super("@" + name, new FuncIrTy(returnTy, fParamTys));
        if (fParamTys == null) {
            return;
        }
        for (int i = 0; i < fParamTys.size(); i++) {
            fParams.add(new FParam(i, fParamTys.get(i)));
        }
    }
    
    public ArrayList<Instruction> getAllInstr() {
        ArrayList<Instruction> res = new ArrayList<>();
        for (BasicBlock bb : bbs) {
            res.addAll(bb.getAllInstr());
        }
        return res;
    }
    
    public LinkedList<BasicBlock> getBbs() {
        return bbs;
    }
    
    public Function(String name, DataIrTy returnTy) {
        super("@" + name, new FuncIrTy(returnTy, new ArrayList<>()));
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
    
    public void addBb2end(BasicBlock basicBlock) {
        bbs.addLast(basicBlock);
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
}