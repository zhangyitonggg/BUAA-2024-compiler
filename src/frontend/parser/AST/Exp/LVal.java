package frontend.parser.AST.Exp;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;

// 左值表达式 LVal → Ident ['[' Exp ']']
public class LVal {
    private Token ident;
    private Exp exp; // 可能为null
    
    public LVal(Token ident, Exp exp) {
        this.ident = ident;
        this.exp = exp;
    }
    
    public LVal(Token ident) {
        this.ident = ident;
        this.exp = null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident);
        if (exp != null) {
            sb.append(Printer.ST(TokenType.LBRACK));
            sb.append(exp.toString());
            sb.append(Printer.ST(TokenType.RBRACK));
        }
        sb.append("<LVal>\n");
        return sb.toString();
    }
}
