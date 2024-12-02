package frontend.checker;

import frontend.checker.symbol.Symbol;
import frontend.lexer.Token;

import java.util.ArrayList;

public class SymbolTable {
    // 存放作用域id
    private int id;
    // 存放父作用域，1号作用域父亲为null
    private SymbolTable father;
    // 存放子作用域
    private ArrayList<SymbolTable> children;
    // 存放当前作用域（不含子作用域）的新建的symbol
    private ArrayList<Symbol> symbols;
    
    public SymbolTable(SymbolTable father, int id) {
        this.id = id;
        this.father = father;
        this.children = new ArrayList<>();
        this.symbols = new ArrayList<>();
    }
    
    public void addChild(SymbolTable child) {
        children.add(child);
    }
    
    public SymbolTable getFather() {
        return this.father;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
    }
    
    // 看symbols，即当前作用域下的符号表是否已经定义
    public boolean has(Token ident) {
        String index = ident.getValue();
        for (Symbol o : symbols) {
            if (o.getName().equals(index)) {
                return true;
            }
        }
        return false;
    }
    
    // 看变量是否曾被定义过
    public boolean isDefined(Token ident) {
        String index = ident.getValue();
        SymbolTable symbolTable = this;
        while(symbolTable != null) {
            if (symbolTable.has(ident)) {
                return true;
            }
            symbolTable = symbolTable.getFather();
        }
        return false;
    }
    
    public Symbol find(Token ident) {
        String index = ident.getValue();
        SymbolTable symbolTable = this;
        while (symbolTable != null) {
            for (Symbol symbol : symbolTable.symbols) {
                if (symbol.getName().equals(index)) {
                    return symbol;
                }
            }
            symbolTable = symbolTable.getFather();
        }
        return null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : symbols) {
            sb.append(id).append(" ");
            sb.append(symbol.toString());
        }
        for (SymbolTable child : children) {
            sb.append(child.toString());
        }
        return sb.toString();
    }
}
