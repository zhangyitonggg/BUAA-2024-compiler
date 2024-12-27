package frontend.parser.AST;

import frontend.lexer.Token;
import frontend.parser.AnyNode;

// 函数类型 FuncType → 'void' | 'int' | 'char'
public class FuncType implements AnyNode {
    Token token;
    
    public FuncType(Token token) {
        this.token = token;
    }
    
    public Token getToken() {
        return token;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(token.toString());
        sb.append("<FuncType>\n");
        return sb.toString();
    }
}
