package frontend;

import java.util.ArrayList;

public class TokenStream {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int p = -1;
    
    public void addToken(Token token) {
        this.tokens.add(token);
    }
    
    public Token next() {
        p++;
        return tokens.get(p);
    }
}
