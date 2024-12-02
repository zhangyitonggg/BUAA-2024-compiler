package frontend.checker.symbol;

import frontend.lexer.Token;

public class Symbol {
    protected String name;
    protected SymType type;
    
    public Symbol() {
    
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(Token name) {
        this.name = name.getValue();
    }
    
    public void setType(SymType type) {
        this.type = type;
    }
}
