package frontend.parser;

import Utils.Error;
import Utils.ErrorLog;
import frontend.lexer.Token;
import frontend.lexer.TokenStream;
import frontend.lexer.TokenType;
import frontend.parser.AST.*;
import frontend.parser.AST.Exp.*;
import frontend.parser.AST.Exp.SPrimaryExp.*;
import frontend.parser.AST.Exp.SUnaryExp.*;
import frontend.parser.AST.SConstInitVal.ConstInitVal;
import frontend.parser.AST.SConstInitVal.CIVConstExp;
import frontend.parser.AST.SConstInitVal.CIVConstExps;
import frontend.parser.AST.SConstInitVal.CIVStringConst;
import frontend.parser.AST.SInitVal.IVExp;
import frontend.parser.AST.SInitVal.IVExps;
import frontend.parser.AST.SInitVal.IVStringConst;
import frontend.parser.AST.SInitVal.InitVal;
import frontend.parser.AST.Stmt.*;

import java.util.ArrayList;

import static frontend.lexer.TokenType.*;

public class Parser {
    private static Parser parser;
    private final TokenStream ts;
    private int tryCnt = 0;
    
    private Parser(TokenStream tokenStream) {
        this.ts = tokenStream;
    }
    
    public static Parser getInstance(TokenStream tokenStream) {
        if (null == parser) {
            parser = new Parser(tokenStream);
        }
        return parser;
    }
    
    /** 编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef **/
    public CompUnit parse() {
        // {Decl}
        ArrayList<Decl> decls = new ArrayList<>();
        while(true) {
            Token t1 = ts.peek(1);
            Token t2 = ts.peek(2);
            Token t3 = ts.peek(3);
            if (t1.is(INTTK) && t2.is(TokenType.MAINTK)) {
                break;
            }
            if ((t1.is(TokenType.VOIDTK) || t1.is(INTTK) ||t1.is(TokenType.CHARTK)) &&
                 t2.is(TokenType.IDENFR) && t3.is(LPARENT)) {
                break;
            }
            decls.add(parseDecl());
        }
        // {FuncDef}
        ArrayList<FuncDef> funcDefs = new ArrayList<>();
        while(true) {
            Token t1 = ts.peek(1);
            Token t2 = ts.peek(2);
            if (t1.is(INTTK) && t2.is(TokenType.MAINTK)) {
                break;
            }
            funcDefs.add(parseFuncDef());
        }
        // MainFuncDef
        MainFuncDef mainFuncDef = parseMainFuncDef();
        return new CompUnit(decls, funcDefs, mainFuncDef);
    }
    
    /** 声明 Decl → ConstDecl | VarDecl **/
    private Decl parseDecl() {
        if (ts.peek().is(TokenType.CONSTTK)) {
            return parseConstDecl();
        } else {
            return parseVarDecl();
        }
    }
    
    /** 常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' **/
    private ConstDecl parseConstDecl() {
        ts.next(); // 越过const
        Token bType = ts.next(); // 读取BType
        ArrayList<ConstDef> constDefs = new ArrayList<>();
        constDefs.add(parseConstDef());
        while(ts.peek().is(TokenType.COMMA)) {
            ts.next();
            constDefs.add(parseConstDef());
        }
        checkParserError(SEMICN);
        return new ConstDecl(bType, constDefs);
    }
    
    /** 常量定义 ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal **/
    private ConstDef parseConstDef() {
        Token ident = ts.next();
        ConstExp constExp = null;
        if (ts.peek().is(TokenType.LBRACK)) {
            ts.next();
            constExp = parseConstExp();
            checkParserError(RBRACK);
        }
        ts.next(); // 越过'='
        ConstInitVal constInitVal = parseConstInitVal();
        return new ConstDef(ident, constExp, constInitVal);
    }
    
    /** 常量初值 ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst **/
    private ConstInitVal parseConstInitVal() {
        if (ts.peek().is(TokenType.STRCON)) {
            Token stringConst = ts.next();
            return new CIVStringConst(stringConst);
        } else if (ts.peek().is(TokenType.LBRACE)) {
            ts.next();
            ArrayList<ConstExp> constExps = new ArrayList<>();
            if (!(ts.peek().is(TokenType.RBRACE))) {
                constExps.add(parseConstExp());
                while(ts.peek().is(TokenType.COMMA)) {
                    ts.next();
                    constExps.add(parseConstExp());
                }
            }
            ts.next(); // 越过'}'
            return new CIVConstExps(constExps);
        } else {
            return new CIVConstExp(parseConstExp());
        }
    }
    
    /** 变量声明 VarDecl → BType VarDef { ',' VarDef } ';' **/
    private VarDecl parseVarDecl() {
        Token bType = ts.next();
        ArrayList<VarDef> varDefs = new ArrayList<>();
        varDefs.add(parseVarDef());
        while(ts.peek().is(TokenType.COMMA)) {
            ts.next();
            varDefs.add(parseVarDef());
        }
        checkParserError(SEMICN);
        return new VarDecl(bType, varDefs);
    }
    
    /** 变量定义 VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '='
     InitVal **/
    private VarDef parseVarDef() {
        Token ident = ts.next();
        ConstExp constExp = null;
        if (ts.peek().is(TokenType.LBRACK)) {
            ts.next();
            constExp = parseConstExp();
            checkParserError(RBRACK);
        }
        InitVal initVal = null;
        if (ts.peek().is(TokenType.ASSIGN)) {
            ts.next();
            initVal = parseInitVal();
        }
        return new VarDef(ident, constExp, initVal);
    }
    
    /** 变量初值 InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst **/
    private InitVal parseInitVal() {
        if (ts.peek().is(TokenType.STRCON)) {
            return new IVStringConst(ts.next());
        } else if (ts.peek().is(TokenType.LBRACE)) {
            ts.next();
            ArrayList<Exp> exps = new ArrayList<>();
            if (!(ts.peek().is(TokenType.RBRACE))) {
                exps.add(parseExp());
                while(ts.peek().is(TokenType.COMMA)) {
                    ts.next();
                    exps.add(parseExp());
                }
            }
            ts.next(); // 越过'}'
            return new IVExps(exps);
        } else {
            return new IVExp(parseExp());
        }
    }
    
    /** 函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block **/
    private FuncDef parseFuncDef() {
        FuncType funcType = parseFuncType();
        Token ident = ts.next();
        FuncFParams funcFParams = null;
        ts.next(); //越过'('
        if (ts.peek().is(TokenType.LBRACE)) { // 无FuncFParams,无')'
            Error error = new Error(ts.last().getLine(), 'j');
            tryAddError(error);
        } else if (ts.peek().is(TokenType.RPARENT)) { // 无FuncFParams,有')'
            ts.next();
        } else { // 有FuncFParams
            funcFParams = parseFuncFParams();
            checkParserError(RPARENT);
        }
        Block block = parseBlock();
        return new FuncDef(funcType, ident, funcFParams, block);
    }
    
    /** 主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block **/
    private MainFuncDef parseMainFuncDef() {
        ts.next();
        ts.next();
        ts.next();
        checkParserError(RPARENT);
        Block block = parseBlock();
        return new MainFuncDef(block);
    }
    
    /** 函数类型 FuncType → 'void' | 'int' | 'char' **/
    private FuncType parseFuncType() {
        Token token = ts.next();
        return new FuncType(token);
    }
    
    /** 函数形参表 FuncFParams → FuncFParam { ',' FuncFParam } **/
    private FuncFParams parseFuncFParams() {
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        funcFParams.add(parseFuncFParam());
        while(ts.peek().is(COMMA)) {
            ts.next();
            funcFParams.add(parseFuncFParam());
        }
        return new FuncFParams(funcFParams);
    }
    
    /** 函数形参 FuncFParam → BType Ident ['[' ']'] **/
    private  FuncFParam parseFuncFParam() {
        Token bType = ts.next();
        Token ident = ts.next();
        boolean isArray = false;
        if (ts.peek().is(LBRACK)) {
            ts.next();
            isArray = true;
            checkParserError(RBRACK);
        }
        return new FuncFParam(bType, ident, isArray);
    }
    
    /** 语句块 Block → '{' { BlockItem } '}' **/
    private Block parseBlock() {
        ts.next();
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        while(!(ts.peek().is(RBRACE))) {
            blockItems.add(parseBlockItem());
        }
        Token t = ts.peek();
        ts.next();
        return new Block(blockItems, t);
    }
    
    /** 语句块项 BlockItem → Decl | Stmt **/
    public BlockItem parseBlockItem() {
        Token temp = ts.peek();
        if (temp.is(CONSTTK) || temp.is(INTTK) || temp.is(CHARTK)) {
            return (BlockItem) parseDecl();
        } else {
            return (BlockItem) parseStmt();
        }
    }
    
    /**
     * 语句 Stmt →
     * | LVal '=' Exp ';'
     * | [Exp] ';'
     * | Block
     * | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
     * | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
     * | 'break' ';' | 'continue' ';'
     * | 'return' [Exp] ';'
     * | LVal '=' 'getint''('')'';'
     * | LVal '=' 'getchar''('')'';'
     * | 'printf''('StringConst {','Exp}')'';'
     **/
    public Stmt parseStmt() {
        if (ts.peek().is(LBRACE)) {
            return parseBlockSubStmt();
        } else if (ts.peek().is(IFTK)) {
            return parseIfSubStmt();
        } else if (ts.peek().is(FORTK)) {
            return parseForSubStmt();
        } else if (ts.peek().is(BREAKTK)) {
            return parseBreakSubStmt();
        } else if (ts.peek().is(CONTINUETK)) {
            return parseContinueSubStmt();
        } else if (ts.peek().is(RETURNTK)) {
            return parseReturnSubStmt();
        } else if (ts.peek().is(PRINTFTK)) {
            return parsePrintfSubStmt();
        }
        /*
        * "[Exp];"一定会落在UnaryExp或者";"。
        * 单目运算符 UnaryOp → '+' | '−' | '!'
        * 基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number | Character
        * 如果是'+','−','!','(',int,char,';',可以直接选定"[Exp];"；否则需要做判断。
        * */
        Token temp = ts.peek();
        if (temp.is(LPARENT) || temp.is(INTTK) || temp.is(CHARTK) || temp.is(SEMICN) ||
                temp.is(PLUS) || temp.is(MINU) || temp.is(NOT)) {
            // 一定是"[Exp];"
            return parseExpSubStmt();
        } else {
            // 此时需要通过回溯特判
            int checkpoint = ts.getPos();
            tryCnt++;
            parseLVal();
            tryCnt--;
            if(ts.peek().is(ASSIGN)) {
                // 此时一定不是"[Exp];"
                ts.next(); // 越过'='
                if (ts.peek().is(GETINTTK)) {
                    ts.setPos(checkpoint);
                    return parseGetintSubStmt();
                } else if (ts.peek().is(GETCHARTK)) {
                    ts.setPos(checkpoint);
                    return parseGetcharSubStmt();
                } else {
                    ts.setPos(checkpoint);
                    return parseAssignSubStmt();
                }
            } else {
                // 此时一定是"[Exp];"
                ts.setPos(checkpoint);
                return parseExpSubStmt();
            }
        }
    }
    
    // [Exp] ';'
    private ExpSubStmt parseExpSubStmt() {
        if (ts.peek().is(SEMICN)) {      // 无Exp时，一定有';'
            ts.next();
            return new ExpSubStmt(null);
        } else {                              // 有Exp
            Exp exp = parseExp();
            checkParserError(SEMICN);
            return new ExpSubStmt(exp);
        }
    }
    
    // LVal '=' Exp ';'
    private AssignSubStmt parseAssignSubStmt() {
        LVal lVal = parseLVal();
        ts.next();
        Exp exp = parseExp();
        checkParserError(SEMICN);
        return new AssignSubStmt(lVal, exp);
    }
    
    // LVal '=' 'getint''('')'';'
    private GetintSubStmt parseGetintSubStmt() {
        LVal lVal = parseLVal();
        ts.next(); // 越过'='
        ts.next(); // 越过函数名
        ts.next(); // 越过'('
        checkParserError(RPARENT);
        checkParserError(SEMICN);
        return new GetintSubStmt(lVal);
    }
    
    // LVal '=' 'getchar''('')'';'
    private GetcharSubStmt parseGetcharSubStmt() {
        LVal lVal = parseLVal();
        ts.next(); // 越过'='
        ts.next(); // 越过函数名
        ts.next(); // 越过'('
        checkParserError(RPARENT);
        checkParserError(SEMICN);
        return new GetcharSubStmt(lVal);
    }
    
    // Block
    private  BlockSubStmt parseBlockSubStmt() {
        Block block = parseBlock();
        return new BlockSubStmt(block);
    }
    
    // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
    private IfSubStmt parseIfSubStmt() {
        ts.next(); // 越过'if'
        ts.next(); // 越过'('
        Cond cond = parseCond();
        checkParserError(RPARENT);
        Stmt ifStmt = parseStmt();
        if (ts.peek().is(ELSETK)) {
            ts.next();
            Stmt elStmt = parseStmt();
            return new IfSubStmt(cond, ifStmt, elStmt);
        } else {
            return new IfSubStmt(cond, ifStmt);
        }
    }
    
    // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    // for 语句不会出现除了 h 类型以外的任何错误
    private ForSubStmt parseForSubStmt() {
        ForStmt lForStmt = null;
        Cond cond = null;
        ForStmt rForStmt = null;
        Stmt stmt = null;
        
        ts.next(); // 越过'for'
        ts.next(); // 越过'('
        if (!(ts.peek().is(SEMICN))) {
            lForStmt = parseForStmt();
        }
        ts.next(); // 越过‘;’
        if (!(ts.peek().is(SEMICN))) {
            cond = parseCond();
        }
        ts.next(); // 越过‘;’
        if (!(ts.peek().is(RPARENT))) {
            rForStmt = parseForStmt();
        }
        ts.next(); // 越过‘)’
        stmt = parseStmt();
        
        return new ForSubStmt(lForStmt, cond, rForStmt, stmt);
    }
    
    // 'break' ';'
    private BreakSubStmt parseBreakSubStmt() {
        Token token = ts.next();
        checkParserError(SEMICN);
        return new BreakSubStmt(token);
    }
    
    // 'continue' ';'
    private ContinueSubStmt parseContinueSubStmt() {
        Token token = ts.next();
        checkParserError(SEMICN);
        return new ContinueSubStmt(token);
    }
    
    // 'return' [Exp] ';'
    private ReturnSubStmt parseReturnSubStmt() {
        Token returnToken = ts.next(); // 越过'return'
        Token t = ts.peek();
        if (t.is(RBRACE)) { // Block结束
            checkParserError(SEMICN);
            return new ReturnSubStmt(null, returnToken);
        }
        // Block未结束，则下面一定是BlockItem
        if (t.is(CONSTTK) || t.is(INTTK) || t.is(CHARTK) ||
            t.is(LBRACE) || t.is(IFTK) || t.is(FORTK) || t.is(BREAKTK) || t.is(CONTINUETK) ||
            t.is(RETURNTK) || t.is(GETINTTK) || t.is(GETCHARTK) || t.is(PRINTFTK)) {
            checkParserError(SEMICN);
            return new ReturnSubStmt(null, returnToken);
        }
        // 下面只需要区分Exp和LVal
        /*
         * "[Exp];"一定会落在UnaryExp或者";"。
         * UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
         * PrimaryExp → '(' Exp ')' | LVal | Number | Character
         * 如果是'+','-','!','(',int,char,';',可以直接选定"[Exp];"；否则需要做判断。
         * */
        if (t.is(SEMICN)) {
            ts.next();
            return new ReturnSubStmt(null, returnToken);
        } else if (t.is(LPARENT) || t.is(INTTK) || t.is(CHARTK) ||
                   t.is(PLUS) || t.is(MINU) || t.is(NOT)) {
            // 一定是"[Exp];"
            Exp exp = parseExp();
            checkParserError(SEMICN);
            return new ReturnSubStmt(exp, returnToken);
        } else {
            // 此时需要通过回溯特判
            int checkpoint = ts.getPos();
            tryCnt++;
            parseLVal();
            tryCnt--;
            if(ts.peek().is(ASSIGN)) {
                // 此时一定不是"[Exp];",也就是缺少分号
                ts.setPos(checkpoint);
                checkParserError(SEMICN);
                return new ReturnSubStmt(null, returnToken);
            } else {
                // 此时一定是"Exp;"
                ts.setPos(checkpoint);
                Exp exp = parseExp();
                checkParserError(SEMICN);
                return new ReturnSubStmt(exp, returnToken);
            }
        }
    }
  
    // 'printf''('StringConst {','Exp}')'';'
    private PrintfSubStmt parsePrintfSubStmt() {
        Token printfToken = ts.next(); // 越过'printf'
        ts.next(); // 越过'('
        Token stringConst = ts.next();
        ArrayList<Exp> exps = new ArrayList<>();
        while(ts.peek().is(COMMA)) {
            ts.next();
            Exp exp = parseExp();
            exps.add(exp);
        }
        checkParserError(RPARENT);
        checkParserError(SEMICN);
        return new PrintfSubStmt(printfToken, stringConst, exps);
    }
    
    /** 语句 ForStmt → LVal '=' Exp **/
    private ForStmt parseForStmt() {
        LVal lVal = parseLVal();
        ts.next();
        Exp exp = parseExp();
        return new ForStmt(lVal, exp);
    }
    
    /** 表达式 Exp → AddExp **/
    private Exp parseExp() {
        AddExp addExp = parseAddExp();
        return new Exp(addExp);
    }
    
    /** 条件表达式 Cond → LOrExp **/
    private Cond parseCond() {
        LOrExp lOrExp = parseLOrExp();
        return new Cond(lOrExp);
    }
    
    /** 左值表达式 LVal → Ident ['[' Exp ']'] **/
    private LVal parseLVal() {
        Token ident = ts.next();
        Exp exp = null;
        if(ts.peek().is(LBRACK)) {
            ts.next();
            exp = parseExp();
            checkParserError(RBRACK);
        }
        return new LVal(ident, exp);
    }
    
    /** 基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number | Character **/
    private PrimaryExp parsePrimaryExp() {
        Token t = ts.peek();
        if (t.is(LPARENT)) {
            ts.next();
            Exp exp = parseExp();
            checkParserError(RPARENT);
            return new PEExp(exp);
        } else if (t.is(INTCON)) {
            Number_ number = parseNumber();
            return new PENumber(number);
        } else if (t.is(CHRCON)) {
            Character_ character = parseCharacter();
            return new PECharacter(character);
        } else {
            LVal lVal = parseLVal();
            return new PELVal(lVal);
        }
    }
    
    /** 数值 Number → IntConst **/
    private Number_ parseNumber() {
        Token intConst = ts.next();
        return new Number_(intConst);
    }
    
    /** 字符 Character → CharConst **/
    private Character_ parseCharacter() {
        Token charConst = ts.next();
        return new Character_(charConst);
    }
    
    /** 一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp **/
    private UnaryExp parseUnaryExp() {
        Token t = ts.peek();
        if (t.is(PLUS) || t.is(MINU) || t.is(NOT)) {
            UnaryOp unaryOp = parseUnaryOp();
            UnaryExp unaryExp = parseUnaryExp();
            return new UEUnary(unaryOp, unaryExp);
        } else if (t.is(IDENFR) && ts.peek(2).is(LPARENT)) {
            Token ident = t;
            FuncRParams funcRParams = null;
            ts.next();
            ts.next();
            t = ts.peek();
            // '+', '-', '!', ident, '(', int, char
            // 有点不严谨，但是参考往年处理方法感觉足够了
            if (t.is(PLUS) || t.is(MINU) || t.is(NOT) || t.is(IDENFR) || t.is(INTCON) || t.is(CHRCON) || t.is(LPARENT)) {
                funcRParams = parseFuncRParams();
            }
            checkParserError(RPARENT);
            return new UEFunc(ident, funcRParams);
        } else {
            PrimaryExp primaryExp = parsePrimaryExp();
            return new UEPrimaryExp(primaryExp);
        }
    }
    
    /** 单目运算符 UnaryOp → '+' | '−' | '!' 注：'!'仅出现在条件表达式中 **/
    private UnaryOp parseUnaryOp() {
        Token token = ts.next();
        return new UnaryOp(token);
    }
    
    private FuncRParams parseFuncRParams() {
        ArrayList<Exp> exps = new ArrayList<>();
        exps.add(parseExp());
        while(ts.peek().is(COMMA)) {
            ts.next();
            exps.add(parseExp());
        }
        return new FuncRParams(exps);
    }
    
    /**
     * 乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
     * 改写为 MulExp → UnaryExp { ('*' | '/' | '%') UnaryExp }
     * **/
    private MulExp parseMulExp() {
        ArrayList<Object> nodes = new ArrayList<>();
        nodes.add(parseUnaryExp());
        while (ts.peek().is(MULT) || ts.peek().is(DIV) || ts.peek().is(MOD)) {
            nodes.add(ts.next());
            nodes.add(parseUnaryExp());
        }
        return new MulExp(nodes);
    }
    
    /**
     * 加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp
     * 改写为： AddExp → MulExp { ('+' | '−') MulExp }
     * **/
    private AddExp parseAddExp() {
        ArrayList<Object> nodes = new ArrayList<>();
        nodes.add(parseMulExp());
        while (ts.peek().is(PLUS) || ts.peek().is(MINU)) {
            nodes.add(ts.next());
            nodes.add(parseMulExp());
        }
        return new AddExp(nodes);
    }
    
    /**
     * 关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
     * 改写为： RelExp → AddExp { ('<' | '>' | '<=' | '>=') AddExp }
     * **/
    private RelExp parseRelExp() {
        ArrayList<Object> nodes = new ArrayList<>();
        nodes.add(parseAddExp());
        while (ts.peek().is(LSS) || ts.peek().is(GRE) || ts.peek().is(LEQ) || ts.peek().is(GEQ)) {
            nodes.add(ts.next());
            nodes.add(parseAddExp());
        }
        return new RelExp(nodes);
    }
    
    /**
     * 相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
     * 改写为： EqExp → RelExp { ('==' | '!=') RelExp }
     * **/
    private EqExp parseEqExp() {
        ArrayList<Object> nodes = new ArrayList<>();
        nodes.add(parseRelExp());
        while (ts.peek().is(EQL) || ts.peek().is(NEQ)) {
            nodes.add(ts.next());
            nodes.add(parseRelExp());
        }
        return new EqExp(nodes);
    }
    
    /**
     * 逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp
     * 改写为： LAndExp → EqExp { && EqExp }
     * **/
    private LAndExp parseLAndExp() {
        ArrayList<Object> nodes = new ArrayList<>();
        nodes.add(parseEqExp());
        while (ts.peek().is(AND)) {
            nodes.add(ts.next());
            nodes.add(parseEqExp());
        }
        return new LAndExp(nodes);
    }
    
    /**
     * 逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp
     * 改写为： LOrExp → LAndExp { || LAndExp }
     * **/
    private LOrExp parseLOrExp() {
        ArrayList<Object> nodes = new ArrayList<>();
        nodes.add(parseLAndExp());
        while (ts.peek().is(OR)) {
            nodes.add(ts.next());
            nodes.add(parseLAndExp());
        }
        return new LOrExp(nodes);
    }
    
    /** 常量表达式 ConstExp → AddExp 注：使用的 Ident 必须是常量 **/
    private ConstExp parseConstExp() {
        AddExp addExp = parseAddExp();
        return new ConstExp(addExp);
    }
    
    private void checkParserError(TokenType type) {
        if (type == SEMICN) {
            if (ts.peek().is(SEMICN)) {
                ts.next();
            } else if(tryCnt == 0) {
                Error error = new Error(ts.last().getLine(), 'i');
                ErrorLog.getInstance().addError(error);
            }
        }
        else if (type == RPARENT) {
            if (ts.peek().is(RPARENT)) {
                ts.next();
            } else if(tryCnt == 0) {
                Error error = new Error(ts.last().getLine(), 'j');
                ErrorLog.getInstance().addError(error);
            }
        } else if (type == RBRACK) {
            if (ts.peek().is(TokenType.RBRACK)) {
                ts.next();
            } else if(tryCnt == 0){
                Error error = new Error(ts.last().getLine(), 'k');
                ErrorLog.getInstance().addError(error);
            }
        }
    }
    
    private void tryAddError(Error error) {
        if(tryCnt > 0) {
            return;
        }
        ErrorLog.getInstance().addError(error);
    }
}
