package frontend.visitor;

import frontend.lexer.Token;
import frontend.visitor.symbol.Symbol;

import java.util.ArrayList;

public class SymbolTable {
    private ArrayList<Symbol> symbols;
    
    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
    }
    
    public boolean has(Token ident) {
        for (Symbol o : symbols) {
            if (o.getName().equals(ident.toString())) {
                return true;
            }
        }
        return false;
    }
}