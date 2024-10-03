package frontend.lexer;

import java.util.ArrayList;

public class TokenStream {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int p = 0;
    
    public void addToken(Token token) {
        this.tokens.add(token);
    }
    
    public Token next() {
        if (p >= tokens.size()) {
            return new Token(TokenType.EOF, "end", -1);
        }
        // System.out.println(p + "  " + tokens.get(p).getLine() + "   " + tokens.get(p));
        return tokens.get(p++);
    }
    
    public Token peek() {
        return peek(1);
    }
    
    public Token peek(int delta) {
        if (p + delta - 1 >= tokens.size()) {
            return new Token(TokenType.EOF, "end", -1);
        }
        return tokens.get(p + delta - 1);
    }
    
    public Token last() {
        if (p <= 0) {
            return new Token(TokenType.EOF, "end", -1);
        }
        return tokens.get(p - 1);
    }
    
    public void setPos(int pos) {
        p = pos;
    }
    
    public int getPos() {
        return p;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Token token : tokens) {
            sb.append(token.toString());
        }
        return sb.toString();
    }
}
