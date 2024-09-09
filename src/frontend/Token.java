package frontend;

public class Token {
    private TokenType type;
    private String value;
    private int line;
    
    public Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }
    
    public TokenType getType() {
        return this.type;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public int getLine() {
        return this.line;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.type.toString());
        sb.append(" ");
        sb.append(value);
        return sb.toString();
    }
}

