package frontend.checker.symbol;

import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AnyNode;

public class Symbol {
    protected String name;
    protected Type type;
    
    public Symbol() {
    
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(Token name) {
        this.name = name.getValue();
    }
    
    public void setType(Type type) {
        this.type = type;
    }
}
