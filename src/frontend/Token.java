package frontend;

public class Token {
    private TokenType type;
    private String value;
    private int line;
    
    // 对于 & 和 | 的情况，type 取 AND 和 OR，但是 value 取 & 和 |
    public Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }
    
    public Token(TokenType type, int value, int line) {
        this.type = type;
        char temp = (char) value;
        this.value = String.valueOf(temp);
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
        sb.append("\n");
        return sb.toString();
    }
}

