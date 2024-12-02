package llvm;

import com.sun.tools.javac.Main;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.*;
import frontend.parser.AST.Exp.*;
import frontend.parser.AST.Exp.SPrimaryExp.*;
import frontend.parser.AST.Exp.SUnaryExp.*;
import frontend.parser.AST.SConstInitVal.CIVConstExp;
import frontend.parser.AST.SConstInitVal.CIVConstExps;
import frontend.parser.AST.SConstInitVal.CIVStringConst;
import frontend.parser.AST.SConstInitVal.ConstInitVal;
import frontend.parser.AST.SInitVal.IVExp;
import frontend.parser.AST.SInitVal.IVExps;
import frontend.parser.AST.SInitVal.IVStringConst;
import frontend.parser.AST.SInitVal.InitVal;
import frontend.parser.AST.Stmt.*;
import llvm.IrSym.IrSymbolTable;
import llvm.types.*;
import llvm.value.constant.ConstArray;
import llvm.value.constant.ConstData;
import llvm.value.constant.Constant;
import llvm.value.instr.*;
import llvm.value.notInstr.*;
import llvm.value.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Visitor extends IrFactory {
    /**
     * 属性定义
     */
    // 单例模式
    public static Visitor visitor;
    // 符号相关
    protected final IrSymbolTable rootTableForDebug = new IrSymbolTable(null);
    protected IrSymbolTable symbolTable; // 当前符号表。之前想的是利用错误处理时的符号表，但是感觉太丑了
    
    protected final Stack<Loop> loopStack = new Stack<>();
    
    // 永远在学习行走的男人圣经的路上
    protected boolean rParamShouldBePtr = false;
    // 表示是否可以计算出确切的值，不妨规定在第一次遇到非显式const时置为true
    protected boolean canCalValueFlag = false;
    private Token ident;
    
    protected Visitor() {
        this.symbolTable = rootTableForDebug;
    }
    
    public static Visitor getInstance() {
        if (visitor == null) {
            visitor = new Visitor();
        }
        return visitor;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 功能实现
     */
    public Module visit(CompUnit compUnit) {
        visitCompUnit(compUnit);
        return module;
    }
    
    /**
     * 编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef
     */
    protected void visitCompUnit(CompUnit compUnit) {
        ArrayList<Decl> decls = compUnit.getDecls();
        ArrayList<FuncDef> funcDefs = compUnit.getFuncDefs();
        MainFuncDef mainFuncDef = compUnit.getMainFuncDef();
        
        for (Decl decl : decls) {
            visitDecl(decl);
        }
        for (FuncDef funcDef : funcDefs) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(mainFuncDef);
    }
    
    /** 声明 Decl → ConstDecl | VarDecl **/
    protected void visitDecl(Decl decl) {
        if (decl instanceof VarDecl) {
            visitVarDecl((VarDecl) decl);
        } else {
            visitConstDecl((ConstDecl) decl);
        }
    }
    
    /**
     * 变量声明 VarDecl → BType VarDef { ',' VarDef } ';'
     */
    protected void visitVarDecl(VarDecl varDecl) {
        Token bType = varDecl.getBType();
        ArrayList<VarDef> varDefs = varDecl.getVarDefs();

        for (VarDef varDef : varDefs) {
            visitVarDef(varDef, bType);
        }
    }
    
    /**
     * 变量定义 VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
     *   %4 = trunc i32 %3 to i8
     *   store i8 %4, i8* %2, align 1
     */
    protected void visitVarDef(VarDef varDef, Token bType) {
        Token ident = varDef.getIdent();
        ConstExp constExp = varDef.getConstExp();
        InitVal initVal = varDef.getInitVal();
        ArrayList<Value> inits = new ArrayList<>();
        if (constExp == null) {
            // 非数组
            if (isGlobal()) {
                // 全局变量的初值表达式也必须是[常量]表达式。
                GlobalVar globalVar;
                if (initVal == null) {
                    // 没有初始值的情况需要手动置0
                    globalVar = makeGlobalVar(ident.getValue(), new ConstData(getDataIrty(bType),0), false);
                } else {
                    // 有初始值的情况则用常量初始值
                    canCalValueFlag = true;
                    inits = visitInitVal(initVal, bType.is(TokenType.CHARTK), 1);
                    canCalValueFlag = false;
                    ConstData oneInit = (ConstData) inits.get(0);
                    globalVar = makeGlobalVar(ident.getValue(),new ConstData(getDataIrty(bType), oneInit.getValue()), false);
                }
                symbolTable.addSymbol(ident.getValue(), globalVar);
            } else {
                // 局部变量的初值表达式是 Exp，可以使用 已经声明的变量。
                // 这里应该先 alloca，再根据条件判断是否需要 trunc or zext, 最后store
                // alloca
                Alloca alloca = makeAlloca(getDataIrty(bType));
                symbolTable.addSymbol(ident.getValue(), alloca);
                // 处理可能存在的初始值
                if (initVal != null) {
                    inits = visitInitVal(initVal, bType.is(TokenType.CHARTK), 1);
                    Value init = inits.get(0);
                    // 这时候需要处理一下位数不匹配的问题
                    Instruction instruction = tryMakeResize(init, getDataIrty(bType));
                    if (instruction == null) {
                        // 如果instruction为null，即无需resize，那接下来使用的就是init
                        makeStore(init, alloca);
                    } else {
                        makeStore(instruction, alloca);
                    }
                }
            }
        } else {
            // 数组
            // 首先需要计算出数组长度，这显然是一个ConstInt
            int length = visitConstExp(constExp);
            // 现在有长度+元素类型，我们可以得到数组类型：
            ArrayIrTy arrayIrTy = new ArrayIrTy(getDataIrty(bType), length);
            if (isGlobal()) {
                // 对于全局数组，这里的处理应当和globalConstArray的处理是一致的
                GlobalVar globalVar;
                if (initVal == null) {
                    // 没有初始化，需要全搞成0
                    globalVar = makeGlobalVar(ident.getValue(), new ConstArray(arrayIrTy), false);
                    symbolTable.addSymbol(ident.getValue(), globalVar);
                } else {
                    // 有初始值
                    canCalValueFlag = true;
                    inits = visitInitVal(initVal, bType.is(TokenType.CHARTK), length); // 返回的应当是ConstData的集合
                    canCalValueFlag = false;
                    ArrayList<Integer> initIntegers = trans2Int(inits);
                    ConstArray constArray = new ConstArray(arrayIrTy, initIntegers);
                    globalVar = makeGlobalVar(ident.getValue(), new ConstArray(arrayIrTy, initIntegers), false);
                    symbolTable.addSymbol(ident.getValue(),globalVar);
                }
            } else {
                // 局部数组
                Alloca alloca = makeAlloca(arrayIrTy);
                symbolTable.addSymbol(ident.getValue(), alloca);
                // 处理可能存在的初始值
                if (initVal != null) {
                    inits = visitInitVal(initVal, bType.is(TokenType.CHARTK), length);
                    // getelementptr 得到一个int*/char*的指针
                    Getelementptr basePtr = makeGetelementptr(alloca);
                    Value init = inits.get(0);
                    // 这时候需要处理一下位数不匹配的问题
                    Instruction instruction = tryMakeResize(init, getDataIrty(bType));
                    if (instruction == null) {
                        // 如果instruction为null，即无需resize，那接下来使用的就是init
                        makeStore(init, basePtr);
                    } else {
                        makeStore(instruction, basePtr);
                    }
                    // 之后利用另一种getelementptr进行定位
                    Getelementptr tempPtr = null;
                    for (int iter = 1; iter < inits.size(); iter++) {
                        tempPtr = makeGetelementptr(basePtr, iter);
                        init = inits.get(iter);
                        // 这时候需要处理一下位数不匹配的问题
                        instruction = tryMakeResize(init, getDataIrty(bType));
                        if (instruction == null) {
                            // 如果instruction为null，即无需resize，那接下来使用的就是init
                            makeStore(init, tempPtr);
                        } else {
                            makeStore(instruction, tempPtr);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 变量初值 InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
     */
    protected ArrayList<Value> visitInitVal(InitVal initVal, Boolean isChar, int length) {
        ArrayList<Value> inits = new ArrayList<>();
        if (initVal instanceof IVExp ivExp) {
            Value init = visitExp(ivExp.getExp());
            inits.add(init);
        } else if (initVal instanceof IVExps ivExps) {
            ArrayList<Exp> exps = ivExps.getExps();
            for (Exp exp : exps) {
                inits.add(visitExp(exp));
            }
            if (isChar) {
                for (int i = 0; i < length - exps.size(); i++) {
                    inits.add(new ConstData(DataIrTy.I8, 0));
                }
            }
        } else {
            ArrayList<Integer> tmps = ((IVStringConst) initVal).getAsciis();
            for (int i = 0; i < tmps.size(); i++) {
                ConstData constData;
                if (isChar) {
                    constData = new ConstData(DataIrTy.I8, tmps.get(i));
                } else {
                    constData = new ConstData(DataIrTy.I32, tmps.get(i));
                }
                inits.add(constData);
            }
            for (int i = 0; i < length - tmps.size(); i++) {
                ConstData constData;
                if (isChar) {
                    constData = new ConstData(DataIrTy.I8, 0);
                } else {
                    constData = new ConstData(DataIrTy.I32, 0);
                }
                inits.add(constData);
            }
        }
        return inits;
    }
    
    /**
     * 常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
     */
    protected void visitConstDecl(ConstDecl constDecl) {
        Token bType = constDecl.getBType();
        ArrayList<ConstDef> constDefs = constDecl.getConstDefs();
        
        for (ConstDef constDef : constDefs) {
            visitConstDef(constDef, bType);
        }
    }
    
    /**
     * 常量定义 ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
     * 这里的参数传递实际上也是一个继承属性 down
     */
    protected void visitConstDef(ConstDef constDef, Token bType) {
        Token ident = constDef.getIdent();
        ConstExp constExp = constDef.getConstExp();
        ConstInitVal constInitVal = constDef.getConstInitVal();;
        ArrayList<Integer> initIntegers = visitConstInitVal(constInitVal);
        if (constExp == null) {
            // 非数组
            // 考虑到如果一个普通变量是const的，那么实际上只需要将其Value（也必然是一个常量）存到符号表中，用的时候直接取出即可。
            ConstData constData = new ConstData(getDataIrty(bType), initIntegers.get(0));
            symbolTable.addSymbol(ident.getValue(), constData);
        } else {
            // 数组
            // 常量数组不需要每一个元素都有初始值，但是未赋值的部分编译器需要将其置0
            // 首先需要计算出数组长度，这显然是一个ConstInt
            int length = visitConstExp(constExp);
            // 现在有长度+元素类型，我们可以得到数组类型：
            ArrayIrTy arrayIrTy = new ArrayIrTy(getDataIrty(bType), length);
            ConstArray constArray = new ConstArray(arrayIrTy, initIntegers);
            if (isGlobal()) {
                // 如果是全局数组，则可以直接初始化，无需 alloca
                GlobalVar globalVar = makeGlobalVar(ident.getValue(), constArray, true);
                // 登记到符号表之中
                symbolTable.addSymbol(ident.getValue(), globalVar);
            } else {
                /**
                 *     %1 = alloca [3 x i32]
                 *     %2 = getelementptr inbounds [3 x i32], [3 x i32]* %1, i32 0, i32 0
                 *     store i32 1, i32* %2
                 *     %3 = getelementptr inbounds i32, i32* %2, i32 1
                 *     store i32 2, i32* %3
                 *     %4 = getelementptr inbounds i32, i32* %3, i32 1
                 *     store i32 3, i32* %4
                 */
                // 如果是局部数组
                // alloca共length长度的空间
                Alloca alloca = makeAlloca(arrayIrTy, constArray);
                symbolTable.addSymbol(ident.getValue(), alloca);
                // getelementptr 得到一个int*的指针
                Getelementptr basePtr = makeGetelementptr(alloca);
                makeStore(constArray.getElement(0), basePtr);
                // 之后利用另一种getelementptr进行定位
                Getelementptr tempPtr = null;
                for (int iter = 1; iter < constArray.getLength(); iter++) {
                    tempPtr = makeGetelementptr(basePtr, iter);
                    makeStore(constArray.getElement(iter), tempPtr);
                }
            }
        }
    }
    
    /**
     * 常量初值 ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
     * 这里直接返回int的一个list
     */
    protected ArrayList<Integer> visitConstInitVal(ConstInitVal constInitVal) {
        ArrayList<Integer> inits = new ArrayList<>();
        if (constInitVal instanceof CIVConstExp civConstExp) {
            int init = visitConstExp(civConstExp.getConstExp());
            inits.add(init);
        } else if (constInitVal instanceof CIVConstExps civConstExps) {
            ArrayList<ConstExp> constExps = civConstExps.getConstExps();
            for (ConstExp constExp : constExps) {
                inits.add(visitConstExp(constExp));
            }
        } else {
            ArrayList<Integer> tmps = ((CIVStringConst) constInitVal).getAsciis();
            for (int i = 0; i < tmps.size(); i++) {
                inits.add(tmps.get(i));
            }
        }
        return inits;
    }
    
    
    /**
     * 函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
     */
    protected void visitFuncDef(FuncDef funcDef) {
        FuncType funcType = funcDef.getFuncType();
        Token ident = funcDef.getIdent();
        FuncFParams funcFParams = funcDef.getFuncFParams(); // 可能为null
        Block block = funcDef.getBlock();
        
        // 新建Function对象并插入符号表
        DataIrTy returnIrTy = getDataIrty(funcType.getToken());
        ArrayList<DataIrTy> fParamIrTys = new ArrayList<>();
        if (funcFParams != null) {
            ArrayList<FuncFParam> funcFParamArrayList = funcFParams.getFuncFParams();
            for (FuncFParam funcFParam : funcFParamArrayList) {
                DataIrTy baseIrTy = getDataIrty(funcFParam.getBType());
                if (funcFParam.getIsArray()) {
                    fParamIrTys.add(new PointerIrTy(baseIrTy));
                } else {
                    fParamIrTys.add(baseIrTy);
                }
            }
        }
        curFunc = makeFunction(ident.getValue(), returnIrTy, fParamIrTys);
        symbolTable.addSymbol(ident.getValue(), curFunc);
        
        // 补充Function对象的属性
        enter();
        // 对于entryBlock，全部是先alloca，再store
        curBb = makeBasicBlock();
        if (funcFParams != null) {
            visitFuncFParams(funcFParams);
        }
        // 分析block, 会产生0至多个basicBlock，产生br指令时新建basicBlock
        visitBlock(block);
        /* 对于有返回值的函数，最后一个基本块最后一行一定有ret
           对于无返回值的函数，则可能需要人工补一个ret      */
        if (funcType.getToken().is(TokenType.VOIDTK)) {
            visitLastInstructionInFunction();
        }
        leave();
    }
    protected void visitLastInstructionInFunction() {
        Instruction instr = curBb.getInstrAtEnd(); // 可能为null，那实际上就是整个函数一个指令都没有
        if (!(instr instanceof Branch || instr instanceof Ret)) {
            // 这里没写Jump，是因为Jump是无条件跳转，一定无法到达Ret
            makeRet(null); // 啥都不返回，因为只有无返回值函数才会调这个函数
        }
    }
    
    /**
     * 主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block
     */
    protected void visitMainFuncDef(MainFuncDef mainFuncDef) {
        Block block = mainFuncDef.getBlock();
        
        curFunc = makeFunction("main", DataIrTy.I32, new ArrayList<>());
        symbolTable.addSymbol("main", curFunc); // 感觉没有必要，但为了形式上的统一性不妨保留
        
        enter();
        curBb = makeBasicBlock();
        visitBlock(block);
        leave();
    }
    
    
    /**
     * 函数形参表 FuncFParams → FuncFParam { ',' FuncFParam }
     */
    protected void visitFuncFParams(FuncFParams funcFParams) {
        ArrayList<FuncFParam> funcFParamArrayList = funcFParams.getFuncFParams();
        for (int i = 0; i < funcFParamArrayList.size(); i++) {
            visitFuncFParam(i, funcFParamArrayList.get(i));
        }
    }
    
    /**
     * 这里实际上只需要ident了，因为其他的信息在前面已经保存到curFunc中了
     * 函数形参 FuncFParam → BType Ident ['[' ']']
     */
    protected void visitFuncFParam(int iter, FuncFParam funcFParam) {
        DataIrTy dataIrTy = curFunc.getFParamTys().get(iter);
        Alloca alloca = makeAlloca(dataIrTy);
        
        // 形参是没法直接使用的，需要store进alloca中使用
        String name = funcFParam.getIdent().getValue();
        symbolTable.addSymbol(name, alloca);
        
        FParam fParam = curFunc.getFParams().get(iter);
        makeStore(fParam, alloca);
    }
    
    /**
     * 语句块 Block → '{' { BlockItem } '}'
     */
    protected void visitBlock(Block block) {
        for (BlockItem blockItem : block.getBlockItems()) {
            visitBlockItem(blockItem);
        }
    }
    
    /**
     * 语句块项 BlockItem → Decl | Stmt
     */
    protected void visitBlockItem(BlockItem blockItem) {
        if (blockItem instanceof Decl) {
            visitDecl((Decl) blockItem);
        } else {
            visitStmt((Stmt) blockItem);
        }
    }
    
    //////////////////////////////////////////////////////////////////////
    /**
     * 语句 Stmt → LVal '=' Exp ';'
     * | [Exp] ';'
     * | Block
     * | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
     * | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')'
     * | 'break' ';' | 'continue' ';'
     * | 'return' [Exp] ';'
     * | LVal '=' 'getint''('')'';'
     * | LVal '=' 'getchar''('')'';'
     * | 'printf''('StringConst {','Exp}')'';'
     */
    protected void visitStmt(Stmt stmt) {
        if (stmt instanceof AssignSubStmt) {
            visitAssignSubStmt((AssignSubStmt) stmt);
        } else if (stmt instanceof  ExpSubStmt) {
            visitExpSubStmt((ExpSubStmt) stmt);
        } else if (stmt instanceof BlockSubStmt) {
            visitBlockSubStmt((BlockSubStmt) stmt);
        } else if (stmt instanceof IfSubStmt) {
            visitIfSubStmt((IfSubStmt) stmt);
        } else if (stmt instanceof ForSubStmt) {
            visitForSubStmt((ForSubStmt) stmt);
        } else if (stmt instanceof BreakSubStmt) {
            visitBreakSubStmt((BreakSubStmt) stmt);
        } else if (stmt instanceof ContinueSubStmt) {
            visitContinueSubStmt((ContinueSubStmt) stmt);
        } else if (stmt instanceof ReturnSubStmt) {
            visitReturnSubStmt((ReturnSubStmt) stmt);
        } else if (stmt instanceof GetintSubStmt) {
            visitGetintSubStmt((GetintSubStmt) stmt);
        } else if (stmt instanceof GetcharSubStmt) {
            visitGetcharSubStmt((GetcharSubStmt) stmt);
        } else if (stmt instanceof PrintfSubStmt) {
            visitPrintfSubStmt((PrintfSubStmt) stmt);
        } else {
            System.out.println("haha,又又出错了。。。");
        }
    }
    
    /**
     * LVal '=' Exp ';'
     * 这里的目的仅仅是生成利用子节点返回来的Value生成store指令
     */
    protected void visitAssignSubStmt(AssignSubStmt assignSubStmt) {
        LVal lVal = assignSubStmt.getLVal();
        Exp exp = assignSubStmt.getExp();
        
        Value vLVal = visitLVal(lVal);
        Value vExp = visitExp(exp);
        
        // 这时候需要处理一下位数不匹配的问题
        Instruction instruction = tryMakeResize(vExp, (DataIrTy) ((PointerIrTy) vLVal.getType()).deRefIrTy);
        
        if (instruction == null) {
            // 如果instruction为null，即无需resize，那接下来使用的就是init
            makeStore(vExp, vLVal);
        } else {
            makeStore(instruction, vLVal);
        }
    }
    
    /**
     * [Exp] ';'
     * 先判断一下exp是不是空，然后再visit
     */
    protected void visitExpSubStmt(ExpSubStmt expSubStmt) {
        Exp exp = expSubStmt.getExp();
        if (exp != null) {
            visitExp(exp);
        }
    }
    
    /**
     * Block
     * 关键是要更新符号表
     */
    protected void visitBlockSubStmt(BlockSubStmt blockSubStmt) {
        Block block = blockSubStmt.getBlock();
        enter();
        visitBlock(block);
        leave();
    }
    
    /**
     * 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
     *
     */
    protected void visitIfSubStmt(IfSubStmt ifSubStmt) {
        Cond cond = ifSubStmt.getCond();
        Stmt ifStmt = ifSubStmt.getIfStmt();
        Stmt elStmt = ifSubStmt.getElStmt();
        
        if (elStmt != null) { // 有else的情况
            BasicBlock successBb = makeBasicBlock(); // if
            BasicBlock failureBb = makeBasicBlock(); // else
            BasicBlock endBb = makeBasicBlock();     // 条件语句 之后
            
            visitCond(cond, successBb, failureBb);
            curBb = successBb;
            visitStmt(ifStmt);
            makeJump(endBb);
            
            curBb = failureBb;
            visitStmt(elStmt);
            makeJump(endBb);
            
            curBb = endBb;
        } else { // 无else的情况
            BasicBlock successBb = makeBasicBlock(); // if
            BasicBlock endBb = makeBasicBlock();     // 条件语句 之后
            
            visitCond(cond, successBb, endBb);
            curBb = successBb;
            visitStmt(ifStmt);
            makeJump(endBb);
            
            curBb = endBb;
        }
    }
    
    /**
     * 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')'
     * 五个基本块：
     *  initBb
     *  condBb
     *  forwardBb
     *  bodyBb
     *  endBb
     */
    protected void visitForSubStmt(ForSubStmt forSubStmt) {
        // get相关属性
        ForStmt lForStmt = forSubStmt.getLForStmt();  // 可能为null
        Cond cond = forSubStmt.getCond();             // 可能为null
        ForStmt rforStmt = forSubStmt.getRForStmt();  // 可能为null
        Stmt stmt = forSubStmt.getStmt();
        
        // 定义基本块
        BasicBlock condBb = makeBasicBlock();
        BasicBlock forwardBb = makeBasicBlock();
        BasicBlock bodyBb = makeBasicBlock();
        BasicBlock endBb = makeBasicBlock();
        Loop loop = new Loop(curBb, condBb, forwardBb, bodyBb, endBb);
        loopStack.push(loop);
        
        // 处理lForStmt
        // curBb 不需要变化
        if (lForStmt != null) {
            visitForStmt(lForStmt);
        }
        makeJump(condBb);
        
        // 处理cond
        curBb = condBb;
        if (cond != null) {
            // 值得注意的是，这里面不需要加入跳转指令，因为cond的visit过程负责生成跳转指令
            visitCond(cond, bodyBb, endBb);
        } else {
            // 错误的思想： 当没有cond时，就需要加入无条件跳转了
            // 错误的做法： makeJump(bodyBb);
            // 如果加入无条件跳转，链接不到后面的块了
            makeJump(bodyBb);
//            makeBranch(new ConstData(DataIrTy.I32, 1), bodyBb, endBb);
        }
        
        // 处理rForStmt
        curBb = forwardBb;
        if (rforStmt != null) {
            visitForStmt(rforStmt);
        }
        makeJump(condBb);
        
        // 处理bodyBb
        curBb = bodyBb;
        visitStmt(stmt);
        makeJump(forwardBb);
        
        // 更新curBb 并 弹栈
        curBb = endBb;
        loopStack.pop();
    }
    
    /**
     * 'break' ';'
     */
    protected void visitBreakSubStmt(BreakSubStmt breakSubStmt) {
//         makeJump(loopStack.peek().getEndBb());
        Loop loop = loopStack.peek();
        makeBranch(new ConstData(DataIrTy.I32, 1), loop.getEndBb(), loop.getForwardBb());
        curBb = new BasicBlock(null, 10086);
    }
    
    /**
     * 'continue' ';'
     */
    protected void visitContinueSubStmt(ContinueSubStmt continueSubStmt) {
//         makeJump(loopStack.peek().getForwardBb());
        Loop loop = loopStack.peek();
        makeBranch(new ConstData(DataIrTy.I32, 0), loop.getEndBb(), loop.getForwardBb());
        curBb = new BasicBlock(null, 10087);
    }
    
    /**
     * 'return' [Exp] ';'
     */
    protected void visitReturnSubStmt(ReturnSubStmt returnSubStmt) {
        Exp exp = returnSubStmt.getExp();
        if (exp == null) {
            makeRet(null);
        } else {
            Value value = visitExp(exp);
            // 调整位宽
            Instruction instruction = tryMakeResize(value, curFunc.getReturnType());
            if (instruction != null) {
                value = instruction;
            }
            makeRet(value);
        }
        curBb = new BasicBlock(null, 10086);
    }
    
    /**
     * LVal '=' 'getint''('')'';'
     * 值得注意的是，这里必然存在LVal
     */
    protected void visitGetintSubStmt(GetintSubStmt getintSubStmt) {
        LVal lVal = getintSubStmt.getLVal();
        Value vLVal = visitLVal(lVal);
        Call call = makeCall(Function.getint, new ArrayList<>());
        Instruction instruction = tryMakeResize(call, (DataIrTy) ((PointerIrTy) vLVal.getType()).deRefIrTy);
        if (instruction == null) {
            makeStore(call, vLVal);
        } else {
            makeStore(instruction, vLVal);
        }
    }
    
    /**
     * LVal '=' 'getchar''('')'';'
     */
    protected void visitGetcharSubStmt(GetcharSubStmt getcharSubStmt) {
        LVal lVal = getcharSubStmt.getLVal();
        Value vLVal = visitLVal(lVal);
        Call call = makeCall(Function.getchar, new ArrayList<>());
        Instruction instruction = tryMakeResize(call, (DataIrTy) ((PointerIrTy) vLVal.getType()).deRefIrTy);
        if (instruction == null) {
            makeStore(call, vLVal);
        } else {
            makeStore(instruction, vLVal);
        }
    }
    
    /**
     * 'printf''('StringConst {','Exp}')'';'
     */
    protected void visitPrintfSubStmt(PrintfSubStmt printfSubStmt) {
        ArrayList<String> strs = printfSubStmt.splitStringConst();
        ArrayList<Exp> exps = printfSubStmt.getExps();
        
        // 第一遍遍历，visit所有Exp，得到参数列表
        ArrayList<Value> rParams = new ArrayList<>();
        for (int i = 0; i < exps.size(); i++) {
            Exp exp = exps.get(i);
            Value value = visitExp(exp);
            // 进行一个可能的位变化
            Instruction instruction = tryMakeResize(value, DataIrTy.I32); // putint 和 putchar 的参数全部是 I32
            if (instruction != null) {
                value = instruction;
            }
            rParams.add(value);
        }
        // 第二遍遍历，将printf转化成putchar+putint+putstr
        int j = 0;
        for (int i = 0; i < strs.size(); i++) {
            String s = strs.get(i);
            if (s.equals("%d")) {
                Value value = rParams.get(j++);
                // 进行一个可能的位变化
                Instruction instruction = tryMakeResize(value,DataIrTy.I32); // putint参数是 I32
                if (instruction != null) {
                    value = instruction;
                }
                ArrayList<Value> tmp = new ArrayList<>();
                tmp.add(value);
                makeCall(Function.putint, tmp);
            } else if (s.equals("%c")) {
                Value value = rParams.get(j++);
                // 进行一个可能的位变化
                Instruction instruction = tryMakeResize(value,DataIrTy.I8); // putch参数是 I8
                if (instruction != null) {
                    value = instruction;
                }
                ArrayList<Value> tmp = new ArrayList<>();
                tmp.add(value);
                makeCall(Function.putch, tmp);
            } else {
                // putstr
                makeStringLiteralAndGetelementptrAndPutstr(s);
            }
        }
    }
    
    /**
     * 语句 ForStmt → LVal '=' Exp
     * 这里和visitAssignSubStmt无区别
     */
    protected void visitForStmt(ForStmt forStmt) {
        LVal lVal = forStmt.getLVal();
        Exp exp = forStmt.getExp();
        
        Value vLVal = visitLVal(lVal);
        Value vExp = visitExp(exp);
        
        // 这时候需要处理一下位数不匹配的问题
        Instruction instruction = tryMakeResize(vExp, (DataIrTy) ((PointerIrTy) vLVal.getType()).deRefIrTy);
        if (instruction == null) {
            // 如果instruction为null，即无需resize，那接下来使用的就是init
            makeStore(vExp, vLVal);
        } else {
            makeStore(instruction, vLVal);
        }
    }
    
    //////////////////////////////////////////////////////////////////////
    // 使用 char 类型进行运算时编译器必须先将 char 扩展为 int 类型再使用扩展后的数进行计算。
    /**
     * 表达式 Exp → AddExp
     */
    protected Value visitExp(Exp exp) {
        return visitAddExp(exp.getAddExp());
    }
    
    /**
     * 加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp
     */
    protected Value visitAddExp(AddExp addExp) {
        if (canCalValueFlag) {
            int res = calcAddExp(addExp);
            return new ConstData(DataIrTy.I32, res);
        } else {
            Value res = tackleAddExp(addExp);
            return res;
        }
    }
        protected int calcAddExp(AddExp addExp) {
            ArrayList<Object> nodes = addExp.getNodes();
            int res = trans2Int(visitMulExp((MulExp) nodes.get(0)));
            for (int i = 2; i < nodes.size(); i+=2) {
                int tmp = trans2Int(visitMulExp((MulExp) nodes.get(i)));
                Token op = (Token) nodes.get(i - 1);
                if (op.is(TokenType.PLUS)) {
                    res = res + tmp;
                } else {
                    res = res - tmp;
                }
            }
            return res;
        }
        protected Value tackleAddExp(AddExp addExp) {
            // 感觉这里不会有需要位扩展的情况，但还是写上吧
            ArrayList<Object> nodes = addExp.getNodes();
            Value res = visitMulExp((MulExp) nodes.get(0));
            if (nodes.size() > 1) {
                // 实际上如果不做运算，也就没必要统一成I32了
                Instruction instruction = tryMakeResize(res, DataIrTy.I32);
                if (instruction != null) {
                    res = instruction;
                }
            }
            for (int i = 2; i < nodes.size(); i+=2) {
                Value tmp = visitMulExp((MulExp) nodes.get(i));
                Instruction instruction = tryMakeResize(tmp, DataIrTy.I32);
                if (instruction != null) {
                    tmp = instruction;
                }
                Token op = (Token) nodes.get(i - 1);
                if (op.is(TokenType.PLUS)) {
                    res = makeCompute(Compute.Op.ADD, res, tmp);
                } else {
                    res = makeCompute(Compute.Op.SUB, res, tmp);
                }
            }
            return res;
        }
    
    /**
     * 乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
     */
    protected Value visitMulExp(MulExp mulExp) {
        if (canCalValueFlag) {
            int res = calcMulExp(mulExp);
            return new ConstData(DataIrTy.I32, res);
        } else {
            Value res = tackleMulExp(mulExp);
            return res;
        }
    }
        protected int calcMulExp(MulExp mulExp) {
            ArrayList<Object> nodes = mulExp.getNodes();
            int res = trans2Int(visitUnaryExp((UnaryExp) nodes.get(0)));
            for (int i = 2; i < nodes.size(); i+=2) {
                // 突然发现这里有一种弹栈的感觉
                int tmp = trans2Int(visitUnaryExp((UnaryExp) nodes.get(i)));
                Token op = (Token) nodes.get(i - 1);
                if (op.is(TokenType.MULT)) {
                    res = res * tmp;
                } else if (op.is(TokenType.DIV)) {
                    res = res / tmp;
                } else {
                    res = res % tmp;
                }
            }
            return res;
        }
        protected Value tackleMulExp(MulExp mulExp) {
            ArrayList<Object> nodes = mulExp.getNodes();
            Value res = visitUnaryExp((UnaryExp) nodes.get(0));
            if (nodes.size() > 0) {
                Instruction instruction = tryMakeResize(res, DataIrTy.I32);
                if (instruction != null) {
                    res = instruction;
                }
            }
            for (int i = 2; i < nodes.size(); i+=2) {
                Value tmp = visitUnaryExp((UnaryExp) nodes.get(i));
                Instruction instruction = tryMakeResize(tmp, DataIrTy.I32);
                if (instruction != null) {
                    tmp = instruction;
                }
                Token op = (Token) nodes.get(i - 1);
                if (op.is(TokenType.MULT)) {
                    res = makeCompute(Compute.Op.MUL, res, tmp);
                } else if (op.is(TokenType.DIV)) {
                    res = makeCompute(Compute.Op.SDIV, res, tmp);
                } else {
                    res = makeCompute(Compute.Op.SREM, res, tmp);
                }
            }
            return res;
        }
    
    /**
     * 一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
     * 相邻两个 UnaryOp 不能相同
     */
    protected Value visitUnaryExp(UnaryExp unaryExp) {
        if (unaryExp instanceof UEPrimaryExp uEPrimaryExp) {
            return visitPrimaryExp(uEPrimaryExp.getPrimaryExp());
        } else if (unaryExp instanceof UEFunc uEFunc) {
            return visitUEFunc(uEFunc);
        } else {
            return visitUEUnary((UEUnary) unaryExp);
        }
    }
    // Ident '(' [FuncRParams] ')'
    // 函数实参表 FuncRParams → Exp { ',' Exp }
    protected Value visitUEFunc(UEFunc uEFunc) {
        String funcName = uEFunc.getIdent().getValue();
        FuncRParams rParamsFromNode = uEFunc.getFuncRParams(); // 可能为null
        
        Value symbol = symbolTable.find(funcName);
        Function function = (Function) symbol;
        ArrayList<DataIrTy> funcFParamTys = function.getFParamTys(); // funcRParams为null时它为empty
        
        ArrayList<Value> rParamsForMake = new ArrayList<>();
        // 大部分情况下，llvm是比c高一个等级的，但是在函数调用的形参中却又相同，这里是值得思考的
        for (int iter = 0; iter < funcFParamTys.size(); iter++) {
            // 只要到了里面，rParamsFromNode可以保证不是null
            DataIrTy type = funcFParamTys.get(iter); // 可能是bit+char+int，也可能是指针
            Exp exp = rParamsFromNode.getExps().get(iter);
            if (funcFParamTys.get(iter).isBCI()) {
                rParamShouldBePtr = false;
            } else {
                rParamShouldBePtr = true;
            }
            Value vExp = visitExp(exp);
            rParamShouldBePtr = false;
            // bci还设计到resize的问题，指针的话就直接报错了
            if (funcFParamTys.get(iter).isBCI()) {
                Value tmp = tryMakeResize(vExp, funcFParamTys.get(iter));
                if (tmp != null) {
                    vExp = tmp;
                }
            }
            rParamsForMake.add(vExp);
        }
        return makeCall(function, rParamsForMake);
    }
    // UnaryOp UnaryExp
    protected Value visitUEUnary(UEUnary uEUnary) {
        if (canCalValueFlag) {
            return new ConstData(DataIrTy.I32, calcUEUnary(uEUnary));
        } else {
            return tackleUEUnary(uEUnary);
        }
    }
        protected int calcUEUnary(UEUnary uEUnary) {
            UnaryOp unaryOp = uEUnary.getUnaryOp();
            Token op = unaryOp.getOp();
            UnaryExp unaryExp = uEUnary.getUnaryExp();
            ConstData constData = (ConstData) visitUnaryExp(unaryExp);
            int res = constData.getValue();
            if (op.is(TokenType.MINU)) {
                res = 0 - res;
            } else if (op.is(TokenType.NOT)) {
                // 直接当初i32处理即可
                if (res == 0) {
                    res = 1;
                }
            }
            return res;
        }
        protected Value tackleUEUnary(UEUnary uEUnary) {
            UnaryOp unaryOp = uEUnary.getUnaryOp();
            Token op = unaryOp.getOp();
            UnaryExp unaryExp = uEUnary.getUnaryExp();
            Value tmp = visitUnaryExp(unaryExp);
            Instruction instruction = tryMakeResize(tmp, DataIrTy.I32);
            if (instruction != null) {
                tmp = instruction;
            }
            Value res;
            if (op.is(TokenType.MINU)) {
                res = makeCompute(Compute.Op.SUB, new ConstData(DataIrTy.I32,0), tmp);
            } else if (op.is(TokenType.NOT)) {
                res = makeIcmp(op, tmp, new ConstData(DataIrTy.I32, 0));
            } else {
                res = tmp;
            }
            return res;
        }
    
    
    /**
     * 基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number | Character
     */
    protected Value visitPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp instanceof PEExp pEExp) {
            return visitExp(pEExp.getExp()); // 直接visitExp就可以了
        } else if (primaryExp instanceof PELVal) {
            return visitPELVal((PELVal) primaryExp);
        } else if (primaryExp instanceof PENumber) {
            PENumber pENumber = (PENumber) primaryExp;
            return new ConstData(DataIrTy.I32, pENumber.getNumber());
        } else {
            PECharacter pECharacter = (PECharacter) primaryExp;
            return new ConstData(DataIrTy.I8, pECharacter.getAscii());
        }
    }
    protected Value visitPELVal(PELVal pELVal) {
        /**
         * 一些思考
         * 根据rwg老师的C语言课程，LVal中的L其实是定位的意思。那么LVal应当是一个指针。
         * 此时就需要根据情况来决定是否load了
         */
        LVal lVal = pELVal.getlVal();
        if (canCalValueFlag) {
            return visitLVal(lVal);
        } else {
            Value res;
            if (rParamShouldBePtr) {
                /**
                 * 考虑我们的文法
                 *  如果进入这个分支，说明此时在函数传参。而应该传过去一个指针，而此时恰是指针，那就不必load了。
                 */
                rParamShouldBePtr = false;
                res = visitLVal(lVal);
            } else {
                res = visitLVal(lVal);
                /**
                 * 这里看似必须有load，其实不然：
                 * 由于我们将非数组常量以<name, ConstData>的形式存在了符号表中，所以当非数组常量作为左值时是无法load的。
                 * 唉，性能和美观总是无法同时得到的，就像功成名就的未来总是难以和花前月下的浪漫兼得。
                 */
                if (res.getType().isBCI() == false) {
                   // 此时一定是指针类型了
                   res = makeLoad(res);
                }
            }
            return res;
        }
    }
    
    /**
     * 左值表达式 LVal → Ident ['[' Exp ']']
     * 按照荣老师的看法，这里应该全返回指针的。但是无奈存在非数组常量这个异端
     * 只可能是：
     *  1. 非数组a
     *  2. 数组a
     *  3. 非数组a[1] a[b] a[b[1]]
     */
    protected Value visitLVal(LVal lVal) {
        Token ident = lVal.getIdent();
        Exp exp = lVal.getExp();
        
        Value vLVal = symbolTable.find(ident.getValue());
        
        // 特殊处理一下非数组常量这个异端
        if (vLVal.getType().isBCI()) {
            return vLVal;
        }
        
        // 接下来就全都是正儿八经的指针类型了
        // 这个时候vLVal应该是指向变量的，值得注意的是这里也可能存在异端
        IrTy deRefIrTy = ((PointerIrTy) vLVal.getType()).deRefIrTy; // irTy解引用后（指向）的类型
        // 如果指向bct的话
        if (deRefIrTy.isBCI()) {
            if (vLVal instanceof GlobalVar globalVar && canCalValueFlag) {
                /**
                 * 第一个条件保证是全局变量，第二个条件保证可计算。
                 * 这样的话就可以确定下面的情况
                 * 1. 利用全局变量来进行非数组常量的初始化
                 * 2. 利用全局变量来进行非数组全局变量的初始化
                 */
                Constant init = (ConstData) globalVar.getInit();
                return init;
            } else {
                return vLVal;
            }
        }
        // 如果指向array的话
        if (deRefIrTy.isArray()) {
            if (canCalValueFlag) {
                // 可以求值确保了这里的exp不会是null
                int index = ((ConstData) visitExp(exp)).getValue();
                if (vLVal instanceof Alloca alloca) {
                    ConstArray inits = (ConstArray) alloca.getInit();
                    ConstData init = inits.getElement(index);
                    return init;
                }
                if (vLVal instanceof GlobalVar globalVar) {
                    ConstArray inits = (ConstArray) globalVar.getInit();
                    ConstData init = inits.getElement(index);
                    return init;
                }
            } else {
                /**
                 * 出现的情况：
                 * 1. 普普通通的a[1];
                 * 2. 传参时的a
                 */
                if (exp != null) {
                    Value delta = visitExp(exp);
                    Getelementptr getelementptr = makeGetelementptr(vLVal, new ConstData(DataIrTy.I32, 0), delta);
                    return getelementptr;
                } else {
                    Getelementptr getelementptr = makeGetelementptr(vLVal);
                    return getelementptr;
                }
            }
        }
        // 如果指向指针
        if (deRefIrTy.isPointer()) {
            /**
             * 目前我认为这种情况只能出现在函数内部。因为似乎只有函数传参时才会出现指向指针的指针
             * 有两种子情况：
             *   1. 存在exp，子函数内部使用传进来的数组元素
             *   2. 不存在exp，子函数内部使用传进来的数组作为新的参数，来调用子子函数
             */
            if (exp != null) {
                Load array = makeLoad(vLVal);
                Value index = visitExp(exp);
                Getelementptr getelementptr = makeGetelementptr(array, index);
                return getelementptr;
            } else {
                Load array = makeLoad(vLVal);
                return array;
            }
        }
        System.out.println("应该没有其他情况了吧");
        return null;
    }
    
    
    /**
     * 条件表达式 Cond → LOrExp
     */
    protected void visitCond(Cond cond, BasicBlock successBb, BasicBlock failureBb) {
        LOrExp lOrExp = cond.getLOrExp();
        visitLOrExp(lOrExp, successBb, failureBb);
    }
    
    /**
     * 逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp
     * 需要遵守短路求值的规则
     * 将 1 转化成 2
     * 1. if (cond1 || cond2)
     * 2. if (cond1) {
     *
     *    } else {
     *          if (cond2) {
     *
     *          } else {
     *
     *          }
     *    }
     */
    protected void visitLOrExp(LOrExp lOrExp, BasicBlock successBb, BasicBlock failureBb) {
        ArrayList<Object> nodes = lOrExp.getNodes();
        for (int i = 0; i < nodes.size(); i+=2) {
            LAndExp lAndExp = (LAndExp) nodes.get(i);
            if (i + 2 < nodes.size()) {
                // 不是最后一个
                BasicBlock nextBlock = makeBasicBlock();
                visitLAndExp(lAndExp, successBb, nextBlock);
                curBb = nextBlock;
            } else {
                visitLAndExp(lAndExp, successBb, failureBb);
            }
        }
    }
    
    /**
     * 逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp
     * 需要遵守短路求值的规则
     * 将 1 转化成 2
     * 1. if (cond1 && cond2)
     * 2. if (cond1)
     *        if (cond2)
     */
    protected void visitLAndExp(LAndExp lAndExp, BasicBlock successBb, BasicBlock failureBb) {
        ArrayList<Object> nodes = lAndExp.getNodes();
        // 首先判断条件
        for (int i = 0; i < nodes.size(); i+=2) {
            EqExp eqExp = (EqExp) nodes.get(i);
            // ans必然是Icmp，也就必然是I1类型
            Value ans = visitEqExp(eqExp);
            
            if (i + 2 < nodes.size()) {
                // 不是最后一个
                BasicBlock nextBb = makeBasicBlock();
                // 成功了就可以接受下一个检验了，否则就直接失败吧
                makeBranch(ans, nextBb, failureBb);
                // 更新curBb
                curBb = nextBb;
            } else {
                // 是最后一个
                makeBranch(ans, successBb, failureBb);
                // 更新curBb，似乎没有必要更新。
                curBb = successBb;
            }
        }
        
    }
    
    /**
     * 相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
     */
    protected Value visitEqExp(EqExp eqExp) {
        ArrayList<Object> nodes = eqExp.getNodes();
        
        RelExp relExp = (RelExp) nodes.get(0);
        Value res = visitRelExp(relExp);
        
        // 将res转化为Icmp
        if (nodes.size() == 1) {
            // 处理了if(a) if(1)
            Value value = tryMakeResize(res, DataIrTy.I32);
            if (value != null) {
                res = value;
            }
            Icmp icmp = makeNeq(res, new ConstData(DataIrTy.I32, 0));
            res = icmp;
        }
        
        for (int i = 2; i < nodes.size(); i+=2) {
            Token op = (Token) nodes.get(i - 1);
            relExp = (RelExp) nodes.get(i);
            Value tmp = visitRelExp(relExp);
            
            Instruction instr1 = tryMakeResize(res, DataIrTy.I32);
            if (instr1 != null) {
                res = instr1;
            }
            Instruction instr2 = tryMakeResize(tmp, DataIrTy.I32);
            if (instr2 != null) {
                tmp = instr2;
            }
            
            res = makeIcmp(op, res, tmp);
        }

        return res;
    }
    
    /**
     * 关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
     *
     */
    protected Value visitRelExp(RelExp relExp) {
        ArrayList<Object> nodes = relExp.getNodes();
        
        AddExp addExp = (AddExp) nodes.get(0);
        Value res = visitAddExp(addExp);
        
        for (int i = 2; i < nodes.size(); i+=2) {
            Token op = (Token) nodes.get(i - 1);
            addExp = (AddExp) nodes.get(i);
            Value tmp = visitAddExp(addExp);
            
            Instruction instr1 = tryMakeResize(res, DataIrTy.I32);
            if (instr1 != null) {
                res = instr1;
            }
            Instruction instr2 = tryMakeResize(tmp, DataIrTy.I32);
            if (instr2 != null) {
                tmp = instr2;
            }
            
            res = makeIcmp(op, res, tmp);
        }
        return res;
    }
    
    /**
     * 常量表达式 ConstExp → AddExp 注：使用的 Ident 必须是常量
     * 必然返回一个常数，那不如直接就返回一个int
     */
    protected int visitConstExp(ConstExp constExp) {
        canCalValueFlag = true;
        ConstData constData = (ConstData) visitAddExp(constExp.getAddExp());
        canCalValueFlag = false;
        return constData.getValue();
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 工具实现
     */
    // 符号相关
    protected void enter() {
        symbolTable = new IrSymbolTable(symbolTable);
    }

    protected void leave() {
        symbolTable = symbolTable.getFather();
    }

    protected boolean isGlobal() {
        return symbolTable.getFather() == null;
    }
    
    protected DataIrTy getDataIrty(Token token) {
        if (token.is(TokenType.CHARTK)) {
            return DataIrTy.I8;
        } else if(token.is(TokenType.INTTK)) {
            return DataIrTy.I32;
        } else if (token.is(TokenType.VOIDTK)) {
            return new VoidIrTy();
        }
        return null;
    }
    
    protected ArrayList<Integer> trans2Int(ArrayList<Value> inits) {
        ArrayList<Integer> res = new ArrayList<>();
        if (inits == null) {
            return res;
        }
        for (Value value : inits) {
            ConstData constData = (ConstData) value;
            res.add(constData.getValue());
        }
        return res;
    }
    
    protected int trans2Int(Value value) {
        ConstData constData = (ConstData) value;
        return constData.getValue();
    }
}
