package frontend.parser.AST;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;

// 函数形参 FuncFParam → BType Ident ['[' ']']
public class FuncFParam {
    private Token bType;
    private Token ident;
    private boolean isArray;
    
    public FuncFParam(Token bType, Token ident, boolean isArray) {
        this.bType = bType;
        this.ident = ident;
        this.isArray = isArray;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bType.toString());
        sb.append(ident.toString());
        if(isArray) {
          sb.append(Printer.ST(TokenType.LBRACK));
          sb.append(Printer.ST(TokenType.RBRACK));
        }
        sb.append("<FuncFParam>\n");
        return sb.toString();
    }
}
