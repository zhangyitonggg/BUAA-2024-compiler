package frontend.visitor.symbol;

import frontend.lexer.Token;
import frontend.visitor.symbol.Symbol;
import frontend.visitor.type.SymType;

public class VarSymbol extends Symbol {
    private boolean isConst;
    private boolean isGlobal;
    private final Integer init; // 为null表示没有初始值
    
    public VarSymbol(Token name, SymType type, boolean isConst, boolean isGlobal, int init) {
        super(name, type);
        this.isConst = isConst;
        this.isGlobal = isGlobal;
        this.init = init;
    }

    public VarSymbol(Token name, SymType type, boolean isConst, boolean isGlobal) {
        super(name, type);
        this.isConst = isConst;
        this.isGlobal = isGlobal;
        this.init = null;
    }
}