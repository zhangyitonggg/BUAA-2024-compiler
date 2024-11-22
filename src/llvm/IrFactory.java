package llvm;

import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.AddExp;
import llvm.types.DataIrTy;
import llvm.types.FuncIrTy;
import llvm.types.IrTy;
import llvm.value.User;
import llvm.value.Value;
import llvm.value.constant.ConstData;
import llvm.value.constant.Constant;
import llvm.value.instr.*;
import llvm.value.notInstr.BasicBlock;
import llvm.value.notInstr.Function;
import llvm.value.notInstr.GlobalVar;
import llvm.value.notInstr.StringLiteral;

import java.util.ArrayList;
import java.util.HashMap;

public class IrFactory {
    protected final Module module = new Module();
    // 用于Instruction和BasicBlock的起名
    private int nameCounter = 0;
    // 用于字符字面量的起名
    private int strCounter = 0;
    // 一个常量字符串池
    private final HashMap<String, StringLiteral> stringLiterals = new HashMap<>();
    
    protected Function curFunc = null;
    protected BasicBlock curBb = null;
    
    protected GlobalVar makeGlobalVar(String name, Constant init, boolean isConst) {
        GlobalVar globalVar = new GlobalVar(name, init, isConst);
        module.addGlobalVar(globalVar);
        return globalVar;
    }
    
    // 常量
    protected Alloca makeAlloca(IrTy baseTy, Constant init) {
        Alloca alloca = new Alloca(curFunc.getFirstBb(), nameCounter++, baseTy, init);
        // curBb.add2end(alloca);
        curFunc.getFirstBb().add2head(alloca);
        return alloca;
    }
    
    // 变量
    protected Alloca makeAlloca(IrTy baseTy) {
        Alloca alloca = new Alloca(curFunc.getFirstBb(),nameCounter++, baseTy);
        // curBb.add2end(alloca);
        curFunc.getFirstBb().add2head(alloca);
        return alloca;
    }
    
    protected Getelementptr makeGetelementptr(Value base) {
        return makeGetelementptr(base, new ConstData(DataIrTy.I32, 0), new ConstData(DataIrTy.I32, 0));
    }
    
    protected Getelementptr makeGetelementptr(Value base, Value delta1, Value delta2) {
        Instruction instruction1 = tryMakeResize(delta1, DataIrTy.I32);
        if (instruction1 != null) {
            delta1 = instruction1;
        }
        Instruction instruction2 = tryMakeResize(delta2, DataIrTy.I32);
        if (instruction2 != null) {
            delta2 = instruction2;
        }
        Getelementptr getelementptr = new Getelementptr(curBb, nameCounter++, base, delta1, delta2);
        curBb.add2end(getelementptr);
        return getelementptr;
    }
    
    protected Getelementptr makeGetelementptr(Value base, int delta) {
        return makeGetelementptr(base, new ConstData(DataIrTy.I32, delta));
    }
    
    protected Getelementptr makeGetelementptr(Value base, Value delta) {
        Instruction instruction = tryMakeResize(delta, DataIrTy.I32);
        if (instruction != null) {
            delta = instruction;
        }
        Getelementptr getelementptr = new Getelementptr(curBb, nameCounter++, base, delta);
        curBb.add2end(getelementptr);
        return getelementptr;
    }
    
    protected void makeStore(Value from, Value to) {
        Store store = new Store(curBb, from, to);
        curBb.add2end(store);
    }
    
    /**
     *
     * @param name
     * @param returnIrTy
     * @param fParamsTys 不允许为null
     * @return
     */
    protected Function makeFunction(String name, DataIrTy returnIrTy, ArrayList<DataIrTy> fParamsTys) {
        Function function = new Function(name, returnIrTy, fParamsTys);
        module.addFunctions(function);
        return function;
    }
    
    protected BasicBlock makeBasicBlock() {
        BasicBlock bb = new BasicBlock(curFunc, nameCounter++);
        curFunc.addBb2end(bb);
        return bb;
    }
    
    /**
     * 返回null则说明无需resize
     * 不适用于目标type为I1的情况
     * @param oriValue
     * @param targetTy
     * @return
     */
    protected Instruction tryMakeResize(Value oriValue, DataIrTy targetTy) {
        if (!oriValue.getType().isBCI()) {
            return null;
        }
        DataIrTy oriTy = (DataIrTy) oriValue.getType();
        Instruction instruction;
        if (oriTy.less(targetTy)) {
            instruction = new Zext(curBb, nameCounter++, oriValue, targetTy);
        } else if (oriTy.more(targetTy)) {
            instruction = new Trunc(curBb,nameCounter++, oriValue, targetTy);
        } else {
            return null;
        }
        curBb.add2end(instruction);
        return instruction;
    }
    
    /**
     * @param value 如果为null那Ret就没有返回值
     */
    protected void makeRet(Value value) {
        Ret ret;
        if (value == null) {
           ret = new Ret(curBb);
       } else {
            ret = new Ret(curBb, value);
        }
        curBb.add2end(ret);
    }
    
    protected void makeJump(BasicBlock toBb) {
        Jump jump = new Jump(curBb, toBb);
        curBb.add2end(jump);
    }
    
    protected void makeBranch(Value cond, BasicBlock trueBb, BasicBlock falseBb) {
        Branch branch = new Branch(curBb, cond, trueBb, falseBb);
        curBb.add2end(branch);
    }
    
    protected Call makeCall(Function function, ArrayList<Value> rParams) {
        if (rParams == null) {
            rParams = new ArrayList<>();
        }
        if (((FuncIrTy) function.getType()).returnTy.isVoid()) {
            Call call = new Call(curBb, function, rParams);
            curBb.add2end(call);
            return call;
        } else {
            Call call = new Call(curBb, nameCounter++, function, rParams);
            curBb.add2end(call);
            return call;
        }
    }
    
    protected StringLiteral makeStringLiteral(String literal) {
        if (stringLiterals.containsKey(literal)) {
            return stringLiterals.get(literal);
        }
        StringLiteral stringLiteral = new StringLiteral(strCounter++, literal);
        stringLiterals.put(literal, stringLiteral);
        module.addStringLiteral(stringLiteral);
        return stringLiteral;
    }
    
    protected void makeStringLiteralAndGetelementptrAndPutstr(String literal) {
        StringLiteral stringLiteral = makeStringLiteral(literal);
        Getelementptr getelementptr = makeGetelementptr(stringLiteral);
        makeCall(Function.putstr, new ArrayList<>(){{
            add(getelementptr);
        }});
    }
    
    protected Compute makeCompute(Compute.Op op,Value leftOperand, Value rightOperand) {
        Compute compute = new Compute(curBb, op, nameCounter++, leftOperand, rightOperand);
        curBb.add2end(compute);
        return compute;
    }
    
    protected Load makeLoad(Value pointer) {
        Load load = new Load(curBb, nameCounter++, pointer);
        curBb.add2end(load);
        return load;
    }
    
    protected Icmp makeNeq(Value leftOperand, ConstData constant) {
        return makeIcmp(new Token(TokenType.NEQ, "!=", 0), leftOperand, constant);
    }
    
    protected Icmp makeIcmp(Token tokenOp, Value leftOperand, Value rightOperand) {
        Icmp.Op op;
        /*
              LSS,        // <         SLT
              LEQ,        // <=        SLE
              GRE,        // >         SGT
              GEQ,        // >=        SGE
              EQL,        // ==        EQ
              NEQ,        // !=        NE
         */
        if (tokenOp.is(TokenType.LSS)) {
            op = Icmp.Op.SLT;
        } else if (tokenOp.is(TokenType.LEQ)) {
            op = Icmp.Op.SLE;
        } else if (tokenOp.is(TokenType.GRE)) {
            op = Icmp.Op.SGT;
        } else if (tokenOp.is(TokenType.GEQ)) {
            op = Icmp.Op.SGE;
        } else if (tokenOp.is(TokenType.EQL)) {
            op = Icmp.Op.EQ;
        } else if (tokenOp.is(TokenType.NEQ)){
            op = Icmp.Op.NE;
        } else {
            // NOT
            op = Icmp.Op.EQ;
        }
        Icmp icmp = new Icmp(curBb, op, nameCounter++, leftOperand, rightOperand);
        curBb.add2end(icmp);
        return icmp;
    }
}
