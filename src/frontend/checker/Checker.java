// 尽可能的将功能封装到Symbol之类中，以便生成中间代码时扩展
// 进入子作用域和离开子作用域都有父作用域进行处理
package frontend.checker;

import Utils.Error;
import Utils.ErrorLog;
import frontend.checker.symbol.FuncSymbol;
import frontend.checker.symbol.Symbol;
import frontend.checker.symbol.Type;
import frontend.checker.symbol.VarSymbol;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.*;
import frontend.parser.AST.Exp.*;
import frontend.parser.AST.Exp.SPrimaryExp.PEExp;
import frontend.parser.AST.Exp.SPrimaryExp.PELVal;
import frontend.parser.AST.Exp.SPrimaryExp.PrimaryExp;
import frontend.parser.AST.Exp.SUnaryExp.UEFunc;
import frontend.parser.AST.Exp.SUnaryExp.UEPrimaryExp;
import frontend.parser.AST.Exp.SUnaryExp.UEUnary;
import frontend.parser.AST.Exp.SUnaryExp.UnaryExp;
import frontend.parser.AST.SConstInitVal.CIVConstExp;
import frontend.parser.AST.SConstInitVal.CIVConstExps;
import frontend.parser.AST.SConstInitVal.ConstInitVal;
import frontend.parser.AST.SInitVal.IVExp;
import frontend.parser.AST.SInitVal.IVExps;
import frontend.parser.AST.SInitVal.InitVal;
import frontend.parser.AST.Stmt.*;
import frontend.parser.AnyNode;
import frontend.parser.Parser;
import frontend.visitor.Visitor;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Checker {
    public static Checker checker;
    private int idCount;
    private SymbolTable rootTable;
    private SymbolTable curTable;
    private int loopNum;
    private boolean voidFuncFlag;
    
    private Checker() {
        this.idCount = 1;
        rootTable = new SymbolTable(null, 1);
        curTable = rootTable;
        loopNum = 0;
        voidFuncFlag = false;
    }
    
    public static Checker getInstance() {
        if (checker == null) {
            checker = new Checker();
        }
        return checker;
    }
    
    
    public SymbolTable check(CompUnit compUnit) {
        checkCompUnit(compUnit);
        return rootTable;
    }
    
    /** 编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef **/
    private void checkCompUnit(CompUnit compUnit) {
        ArrayList<Decl> decls = compUnit.getDecls();
        ArrayList<FuncDef> funcDefs = compUnit.getFuncDefs();
        MainFuncDef mainFuncDef = compUnit.getMainFuncDef();
        
        for (Decl decl : decls) {
            checkDecl(decl);
        }
        for (FuncDef funcDef : funcDefs) {
            checkFuncDef(funcDef);
        }
        checkMainFuncDef(mainFuncDef);
    }
    
    /** 声明 Decl → ConstDecl | VarDecl **/
    private void checkDecl(Decl decl) {
        if (decl instanceof ConstDecl) {
            checkConstDecl((ConstDecl) decl);
        } else {
            checkVarDecl((VarDecl) decl);
        }
    }
    
    /** 常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' // i **/
    private void checkConstDecl(ConstDecl constDecl) {
        Token bType = constDecl.getBType();
        ArrayList<ConstDef> constDefs = constDecl.getConstDefs();
        
        for (ConstDef constDef : constDefs) {
            checkConstDef(constDef, bType);
        }
    }
    
    /** 常量定义 ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal // b k **/
    private void checkConstDef(ConstDef constDef, Token bType) {
        Token ident = constDef.getIdent();
        ConstExp constExp = constDef.getConstExp();
        ConstInitVal constInitVal = constDef.getConstInitVal();
        
        if (curTable.has(ident)) {
            addError(ident, 'b');
        }
        
        Symbol symbol = new VarSymbol(ident, bType, constExp, true, isGlobal());
        curTable.addSymbol(symbol);
        
        if (constExp != null) {
            checkConstExp(constExp);
        }
        checkConstInitVal(constInitVal);
    }
    
    /** 常量初值 ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' |
     StringConst **/
    private void checkConstInitVal(ConstInitVal constInitVal) {
        if (constInitVal instanceof CIVConstExp) {
            ConstExp constExp = ((CIVConstExp) constInitVal).getConstExp();
            checkConstExp(constExp);
        } else if (constInitVal instanceof CIVConstExps) {
            ArrayList<ConstExp> constExps = ((CIVConstExps) constInitVal).getConstExps();
            for (ConstExp constExp : constExps) {
                checkConstExp(constExp);
            }
        } else { // StringConst
            // pass
        }
    }
    
    
    /** 变量声明 VarDecl → BType VarDef { ',' VarDef } ';' // i **/
    private void checkVarDecl(VarDecl varDecl) {
        Token bType = varDecl.getBType();
        ArrayList<VarDef> varDefs = varDecl.getVarDefs();
        
        for (VarDef varDef : varDefs) {
            checkVarDef(varDef, bType);
        }
    }
    
    /** 变量定义 VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '='
     InitVal // b k **/
    private void checkVarDef(VarDef varDef, Token bType) {
        Token ident = varDef.getIdent();
        ConstExp constExp = varDef.getConstExp();
        InitVal initVal = varDef.getInitVal();;
        
        if (curTable.has(ident)) {
            addError(ident, 'b');
        }
        
        Symbol symbol = new VarSymbol(ident, bType, constExp, false, isGlobal());
        curTable.addSymbol(symbol);
        
        if (constExp != null) {
            checkConstExp(constExp);
        }
        checkInitVal(initVal);
    }
    
    /** 变量初值 InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst **/
    private void checkInitVal(InitVal initVal) {
        if (initVal instanceof IVExp) {
            Exp exp = ((IVExp) initVal).getExp();
            checkExp(exp);
        } else if (initVal instanceof IVExps) {
            ArrayList<Exp> exps = ((IVExps) initVal).getExps();
            for (Exp exp : exps) {
                checkExp(exp);
            }
        } else { // StringConst
            // pass
        }
    }
    
    
    /** 函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block // b g j **/
    private void checkFuncDef(FuncDef funcDef) {
        FuncType funcType = funcDef.getFuncType();
        Token ident = funcDef.getIdent();
        FuncFParams funcFParams = funcDef.getFuncFParams();
        Block block = funcDef.getBlock();
        
        if (curTable.has(ident)) {
            addError(ident, 'b');
        }
        ArrayList<BlockItem> items = block.getBlockItems();
        if (funcType.getToken().is(TokenType.VOIDTK)) {
            // 在Return语句内检查有无return有返回值
            voidFuncFlag = true;
        } else {
            // 检查有无return
            if (items.isEmpty() || !(items.get(items.size() - 1) instanceof ReturnSubStmt)) {
                addError(block.getRE(), 'g');
            }
        }
        
        Symbol symbol = new FuncSymbol(ident, funcType, funcFParams);
        curTable.addSymbol(symbol);
    
        enter(); // 进入子作用域
        if (funcFParams != null) {
            checkFuncFParams(funcFParams);
        }
        checkBlock(block);
        voidFuncFlag = false;
        leave();
    }
    
    /** 主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block // g j **/
    private void checkMainFuncDef(MainFuncDef mainFuncDef) {
        Block block = mainFuncDef.getBlock();
        
        // 检查有无return
        ArrayList<BlockItem> items = block.getBlockItems();
        if (items.isEmpty() || !(items.get(items.size() - 1) instanceof ReturnSubStmt)) {
            addError(block.getRE(), 'g');
        }
        
        enter(); // 进入子作用域
        checkBlock(block);
        leave();
    }
    
    /** 函数形参表 FuncFParams → FuncFParam { ',' FuncFParam } **/
    private void checkFuncFParams(FuncFParams funcFParams) {
        for (FuncFParam funcFParam : funcFParams.getFuncFParams()) {
            checkFuncFParam(funcFParam);
        }
    }
    
    /** 函数形参 FuncFParam → BType Ident ['[' ']'] // b k **/
    private void checkFuncFParam(FuncFParam funcFParam) {
        Token ident = funcFParam.getIdent();
        Token bType = funcFParam.getBType();
        Boolean isArray = funcFParam.getIsArray();
        
        if (curTable.has(ident)) {
            addError(ident, 'b');
        }
        
        Symbol symbol = new VarSymbol(ident, bType, isArray);
        curTable.addSymbol(symbol);
    }
    
    /** 语句块 Block → '{' { BlockItem } '}' **/
    private void checkBlock(Block block) {
        for (BlockItem blockItem : block.getBlockItems()) {
            checkBlockItem(blockItem);
        }
    }
    
    /** 语句块项 BlockItem → Decl | Stmt **/
    private void checkBlockItem(BlockItem blockItem) {
        if (blockItem instanceof Decl) {
            checkDecl((Decl) blockItem);
        } else {
            checkStmt((Stmt) blockItem);
        }
    }
    
    /**
     * 语句 Stmt → LVal '=' Exp ';' // h i
     * | [Exp] ';' // i
     * | Block
     * | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
     * | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // h
     * | 'break' ';' | 'continue' ';' // i m
     * | 'return' [Exp] ';' // f i
     * | LVal '=' 'getint''('')'';' // h i j
     * | LVal '=' 'getchar''('')'';' // h i j
     * | 'printf''('StringConst {','Exp}')'';' // i j l
     */
    private void checkStmt(Stmt stmt) {
        if (stmt instanceof AssignSubStmt) {
            checkAssignSubStmt((AssignSubStmt) stmt);
        } else if (stmt instanceof  ExpSubStmt) {
            checkExpSubStmt((ExpSubStmt) stmt);
        } else if (stmt instanceof BlockSubStmt) {
            checkBlockSubStmt((BlockSubStmt) stmt);
        } else if (stmt instanceof IfSubStmt) {
            checkIfSubStmt((IfSubStmt) stmt);
        } else if (stmt instanceof ForSubStmt) {
            checkForSubStmt((ForSubStmt) stmt);
        } else if (stmt instanceof BreakSubStmt) {
            checkBreakSubStmt((BreakSubStmt) stmt);
        } else if (stmt instanceof ContinueSubStmt) {
            checkContinueSubStmt((ContinueSubStmt) stmt);
        } else if (stmt instanceof ReturnSubStmt) {
            checkReturnSubStmt((ReturnSubStmt) stmt);
        } else if (stmt instanceof GetintSubStmt) {
            checkGetintSubStmt((GetintSubStmt) stmt);
        } else if (stmt instanceof GetcharSubStmt) {
            checkGetcharSubStmt((GetcharSubStmt) stmt);
        } else if (stmt instanceof PrintfSubStmt) {
            checkPrintfSubStmt((PrintfSubStmt) stmt);
        } else {
            System.out.println("heihei,又出错了。。。");
        }
    }
    /** LVal '=' Exp ';' // h i **/
    private void checkAssignSubStmt(AssignSubStmt assignSubStmt) {
        LVal lVal = assignSubStmt.getLVal();
        Exp exp = assignSubStmt.getExp();
        
        Token ident = lVal.getIdent();
        VarSymbol symbol = (VarSymbol) curTable.find(ident);
        if (symbol != null) {
            if (symbol.isConst()) {
                addError(ident, 'h');
            }
        }
        
        checkLVal(lVal);
        checkExp(exp);
    }
    /** [Exp] ';' // i **/
    private void checkExpSubStmt(ExpSubStmt expSubStmt) {
        Exp exp = expSubStmt.getExp();
        if (exp != null) {
            checkExp(exp);
        }
    }
    /** Block **/
    private void checkBlockSubStmt(BlockSubStmt blockSubStmt) {
        Block block = blockSubStmt.getBlock();
        enter();
        checkBlock(block);
        leave();
    }
    /** 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j **/
    private void checkIfSubStmt(IfSubStmt ifSubStmt) {
        Cond cond = ifSubStmt.getCond();
        Stmt ifStmt = ifSubStmt.getIfStmt();
        Stmt elStmt = ifSubStmt.getElStmt();
        
        checkCond(cond);
        checkStmt(ifStmt);
        if (elStmt != null) {
            checkStmt(elStmt);
        }
    }
    /**
     * 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // h
     *  'h'错误完全可以留给ForStmt和Stmt解决叭
     * **/
    private void checkForSubStmt(ForSubStmt forSubStmt) {
        ForStmt lForStmt = forSubStmt.getLForStmt();
        Cond cond = forSubStmt.getCond();
        ForStmt rforStmt = forSubStmt.getRForStmt();
        Stmt stmt = forSubStmt.getStmt();
        
        if (lForStmt != null) {
            checkForStmt(lForStmt);
        }
        if (cond != null) {
            checkCond(cond);
        }
        if (rforStmt != null) {
            checkForStmt(rforStmt);
        }
        loopNum++;
        checkStmt(stmt);
        loopNum--;
    }
    /** 'break' ';' // i m **/
    private void checkBreakSubStmt(BreakSubStmt breakSubStmt) {
        if (loopNum <= 0) {
            addError(breakSubStmt.getBreakToken(), 'm');
        }
    }
    /** 'continue' ';' // i m **/
    private void checkContinueSubStmt(ContinueSubStmt continueSubStmt) {
        if (loopNum <= 0) {
            addError(continueSubStmt.getContinueToken(), 'm');
        }
    }
    /**
     * 'return' [Exp] ';' // f i
     * **/
    private void checkReturnSubStmt(ReturnSubStmt returnSubStmt) {
        Exp exp = returnSubStmt.getExp();
        if (exp != null) {
            checkExp(exp);
        }
        // 在这里检查'f'类错误
        if (voidFuncFlag) {
            if (returnSubStmt.getExp() != null) {
                addError(returnSubStmt.getReturnToken(), 'f');
            }
        }
    }
    /** LVal '=' 'getint''('')'';' // h i j **/
    private void checkGetintSubStmt(GetintSubStmt getintSubStmt) {
        LVal lVal = getintSubStmt.getLVal();
        
        Token ident = lVal.getIdent();
        VarSymbol symbol = (VarSymbol) curTable.find(ident);
        if (symbol != null) {
            if (symbol.isConst()) {
                addError(ident, 'h');
            }
        }
        
        checkLVal(lVal);
    }
    /** LVal '=' 'getchar''('')'';' // h i j **/
    private void checkGetcharSubStmt(GetcharSubStmt getcharSubStmt) {
        LVal lVal = getcharSubStmt.getLVal();
        
        Token ident = lVal.getIdent();
        VarSymbol symbol = (VarSymbol) curTable.find(ident);
        if (symbol != null) {
            if (symbol.isConst()) {
                addError(ident, 'h');
            }
        }
        
        checkLVal(lVal);
    }
    /**
     * 'printf''('StringConst {','Exp}')'';' // i j l
     * 只看 %d 和 %c
     * **/
    private void checkPrintfSubStmt(PrintfSubStmt printfSubStmt) {
        Token StringConst = printfSubStmt.getStringConst();
        ArrayList<Exp> exps = printfSubStmt.getExps();
        
        int num = 0;
        String value = StringConst.getValue();
        String regex = "%[dc]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            num++;
        }
        if (num != exps.size()) {
            addError(printfSubStmt.getPrintfToken(), 'l');
        }
        
        for (Exp exp : exps) {
            checkExp(exp);
        }
    }
    
    /** 语句 ForStmt → LVal '=' Exp // h **/
    private void checkForStmt(ForStmt forStmt) {
        LVal lVal = forStmt.getLVal();
        Exp exp = forStmt.getExp();
        
        Token ident = lVal.getIdent();
        VarSymbol symbol = (VarSymbol) curTable.find(ident);
        if (symbol != null) {
            if (symbol.isConst()) {
                addError(ident, 'h');
            }
        }
        
        checkLVal(lVal);
        checkExp(exp);
    }
    
    /** 表达式 Exp → AddExp **/
    private void checkExp(Exp exp) {
        AddExp addExp = exp.getAddExp();
        checkAddExp(addExp);
    }
    
    /** 条件表达式 Cond → LOrExp **/
    private void checkCond(Cond cond) {
        LOrExp lOrExp = cond.getLOrExp();
        checkLOrExp(lOrExp);
    }
    
    /** 左值表达式 LVal → Ident ['[' Exp ']'] // c k **/
    private void checkLVal(LVal lVal) {
        Token ident = lVal.getIdent();
        Exp exp = lVal.getExp();
        
        if (!curTable.isDefined(ident)) {
            addError(ident, 'c');
        }
        
        if (exp != null) {
            checkExp(exp);
        }
    }
    
    /** 基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number | Character// j **/
    private void checkPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp instanceof PEExp) {
            checkExp(((PEExp) primaryExp).getExp());
        } else if (primaryExp instanceof PELVal) {
            checkLVal(((PELVal) primaryExp).getlVal());
        }
        // Number 和 Character 的情况无需check
    }
    
    /**
     * 一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp // c d e j
     * ‘c’'d''e'错误由第二类情况处理
     * **/
    private void checkUnaryExp(UnaryExp unaryExp) {
        if (unaryExp instanceof UEPrimaryExp) {
            checkPrimaryExp(((UEPrimaryExp) unaryExp).getPrimaryExp());
        } else if (unaryExp instanceof UEFunc) {
            checkUEFunc((UEFunc) unaryExp);
        } else if (unaryExp instanceof UEUnary) {
            checkUEUnary((UEUnary) unaryExp);
        }
    }
    // 函数调用 Ident '(' [FuncRParams] ')'  c d e
    // 这里容易出bug
    private void checkUEFunc(UEFunc uEFunc) {
        Token ident = uEFunc.getIdent();
        FuncRParams funcRParams = uEFunc.getFuncRParams(); // 可能为null
        
        FuncSymbol funcSymbol = (FuncSymbol) rootTable.find(ident);
        // 识别c类错误
        if (funcSymbol == null) {
            addError(ident, 'c');
        } else {
            // 识别函数调用错误
            // 报错行号均为ident所在行号
            ArrayList<Exp> params = new ArrayList<>();
            if (funcRParams != null) {
                params = funcRParams.getExps();
            }
            // 识别d类错误
            if (funcSymbol.getSize() != params.size()) {
                addError(ident, 'd');
            }
            // 识别e类错误
            /*
             * 传递数组给变量。
             * 传递变量给数组。
             * 传递 char 型数组给 int 型数组。
             * 传递 int 型数组给 char 型数组。
             */
            int minSize = Math.min(funcSymbol.getSize(), params.size());
            for (int i = 0; i < minSize; i++) {
                Exp param = params.get(i);
                boolean isInt = false;
                // 先看看是不是数组,事实上也只有数组需要判断是int还是char
                boolean isArray = false;
                // isArray <=> 遵循这个推导链路：Exp->AddExp->MulExp->UnaryExp->UEPrimaryExp->PrimaryExp->PELVal->LVal->Ident(不含中括号)
                Token paramIdent = param.tryGetIdent(); // 可能为null,表示一定不是数组
                if (paramIdent != null) {
                    if (curTable.find(paramIdent) == null) {
                        // 如果是null,说明存在未定义错误，那么一定不存在e类错误
                        continue;
                    }
                    else if (curTable.find(paramIdent) instanceof VarSymbol) {
                        VarSymbol paramSymbol = (VarSymbol) (curTable.find(paramIdent));
                        isInt = paramSymbol.isInt();
                        isArray = paramSymbol.isArray();
                    }
                }
                // 现在得到了isArray,并得到了数组情况下的isInt,可以识别错误了
                if (!funcSymbol.checkType(i, isArray, isInt)) {
                    addError(ident, 'e');
                }
            }
        }
        
        if (funcRParams != null) {
            checkFuncRParams(funcRParams);
        }
    }
    // UnaryOp UnaryExp
    private void checkUEUnary(UEUnary uEUnary) {
        UnaryExp unaryExp = uEUnary.getUnaryExp();
        checkUnaryExp(unaryExp);
    }
    
    /** 函数实参表 FuncRParams → Exp { ',' Exp } **/
    private void checkFuncRParams(FuncRParams funcRParams) {
        ArrayList<Exp> exps = funcRParams.getExps();
        
        for (Exp exp : exps) {
            checkExp(exp);
        }
    }
    
    /** 乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp **/
    private void checkMulExp(MulExp mulExp) {
        ArrayList<Object> nodes = mulExp.getNodes();
        
        for (int i = 0; i < nodes.size(); i+=2) {
            UnaryExp unaryExp = (UnaryExp) nodes.get(i);
            checkUnaryExp(unaryExp);
        }
    }
    
    /** 加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp **/
    private void checkAddExp(AddExp addExp) {
        ArrayList<Object> nodes = addExp.getNodes();
        
        for (int i = 0; i < nodes.size(); i+=2) {
            MulExp mulExp = (MulExp) nodes.get(i);
            checkMulExp(mulExp);
        }
    }
    
    /** 关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp **/
    private void checkRelExp(RelExp relExp) {
        ArrayList<Object> nodes = relExp.getNodes();
        
        for (int i = 0; i < nodes.size(); i+=2) {
            AddExp addExp = (AddExp) nodes.get(i);
            checkAddExp(addExp);
        }
    }
    
    /** 相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp **/
    private void checkEqExp(EqExp eqExp) {
        ArrayList<Object> nodes = eqExp.getNodes();
        
        for (int i = 0; i < nodes.size(); i+=2) {
            RelExp relExp = (RelExp) nodes.get(i);
            checkRelExp(relExp);
        }
    }
    
    /** 逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp // a **/
    private void checkLAndExp(LAndExp lAndExp) {
        ArrayList<Object> nodes = lAndExp.getNodes();
        
        for (int i = 0; i < nodes.size(); i+=2) {
            EqExp eqExp = (EqExp) nodes.get(i);
            checkEqExp(eqExp);
        }
    }
    
    
    /** 逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp // a **/
    private void checkLOrExp(LOrExp lOrExp) {
        ArrayList<Object> nodes = lOrExp.getNodes();
        
        for (int i = 0; i < nodes.size(); i+=2) {
            LAndExp lAndExp = (LAndExp) nodes.get(i);
            checkLAndExp(lAndExp);
        }
    }
    
    /** 常量表达式 ConstExp → AddExp 注：使用的 Ident 必须是常量 **/
    private void checkConstExp(ConstExp constExp) {
        AddExp addExp = constExp.getAddExp();
        checkAddExp(addExp);
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////
    private void enter() {
        idCount++;
        SymbolTable temp = new SymbolTable(curTable, idCount);
        curTable.addChild(temp);
        curTable = temp;
    }
    
    private void leave() {
        curTable = curTable.getFather();
    }
    
    private void addError(Token token, char errorKey) {
        String all = "bcdefghlm";
        if (!all.contains(Character.toString(errorKey))) {
            System.out.println("又粗心了吧");
        }
        Error error = new Error(token.getLine(), errorKey);
        ErrorLog.getInstance().addError(error);
    }
    
    private boolean isGlobal() {
        return curTable.getId() == 1;
    }
}
