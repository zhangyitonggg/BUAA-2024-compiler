package frontend.visitor.symbol;

import frontend.lexer.Token;
import frontend.visitor.type.SymType;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<SymType> paramTypes;
    
    public FuncSymbol(Token name, SymType returnType, ArrayList<SymType> paramTypes) {
        super(name, returnType);
        this.paramTypes = paramTypes;
    }
}
