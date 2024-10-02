package frontend.lexer;

import java.util.ArrayList;

public class TokenStream {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int p = 0;
    
    public void addToken(Token token) {
        this.tokens.add(token);
    }
    
    public Token next() {
        return tokens.get(p++);
    }
    
    public Token peek() {
        return tokens.get(p);
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
