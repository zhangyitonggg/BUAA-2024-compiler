package frontend.visitor;

import frontend.lexer.Token;
import frontend.parser.AST.*;
import frontend.parser.Parser;

import java.util.ArrayList;

public class Visitor {
    public static Visitor visitor;
    private int id = 1; // 记录作用域序号
    public SymbolTable curTable = new SymbolTable();
    private ArrayList<SymbolTable> stacks = new ArrayList<>();
    
    
    private Visitor() {}
    
    public Visitor getInstance() {
        if (visitor == null) {
            visitor = new Visitor();
        }
        return visitor;
    }
    
    /** 访问每一个非终结符 -> 维护符号表 + 生成中间代码 **/
    public void visit(CompUnit compUnit) {
        // 初始化
        stacks.add(curTable);
        // 语义分析，启动
        visitCompUnit(compUnit);
    }
    
    /** 编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef **/
    public void visitCompUnit(CompUnit compUnit) {
        for (Decl decl : compUnit.getDecls()) {
            visitDecl(decl);
        }
        for (FuncDef funcDef : compUnit.getFuncDefs()) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(compUnit.getMainFuncDef());
    }
    
    /** 声明 Decl → ConstDecl | VarDecl **/
    public void visitDecl(Decl decl) {
        if (decl instanceof ConstDecl) {
            visitConstDecl((ConstDecl) decl);
        } else {
            visitVarDecl((VarDecl) decl);
        }
    }
    
    /** 常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' **/
    public void visitConstDecl(ConstDecl constDecl) {
    
    }
    
    
    /** 变量声明 VarDecl → BType VarDef { ',' VarDef } ';' **/
    public void visitVarDecl(VarDecl varDecl) {
    
    }
    
    public void visitFuncDef(FuncDef funcDef) {
    
    }
    
    public void visitMainFuncDef(MainFuncDef mainFuncDef) {
    
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////
    public void push() {
        curTable = new SymbolTable();
        stacks.add(curTable);
        id++;
    }
    
    public void pop() {
        stacks.remove(stacks.size() - 1);
        curTable = stacks.get(stacks.size() - 1);
    }
    
    public boolean has(Token ident) {
        for (SymbolTable table : stacks) {
            if (table.has(ident)) {
                return true;
            }
        }
        return false;
    }
    
}
