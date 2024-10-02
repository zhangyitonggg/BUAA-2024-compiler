package frontend.parser.AST;

import frontend.lexer.Token;

// 函数类型 FuncType → 'void' | 'int' | 'char'
public class FuncType {
    Token token;
    
    public FuncType(Token token) {
        this.token = token;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(token.toString());
        sb.append("<FuncType>\n");
        return sb.toString();
    }
}
