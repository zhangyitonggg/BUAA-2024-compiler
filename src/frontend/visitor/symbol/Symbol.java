package frontend.visitor.symbol;

import frontend.lexer.Token;
import frontend.visitor.type.SymType;

public class Symbol {
    private String name;
    private SymType type;
    
    public Symbol(Token name, SymType type) {
        this.name = name.getValue();
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
}
