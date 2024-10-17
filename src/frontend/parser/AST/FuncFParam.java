package frontend.parser.AST;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AnyNode;

// 函数形参 FuncFParam → BType Ident ['[' ']']
public class FuncFParam implements AnyNode {
    private Token bType;
    private Token ident;
    private boolean isArray;
    
    public FuncFParam(Token bType, Token ident, boolean isArray) {
        this.bType = bType;
        this.ident = ident;
        this.isArray = isArray;
    }
    
    public Token getBType() {
        return bType;
    }
    
    public Token getIdent() {
        return ident;
    }
    
    public boolean getIsArray() {
        return isArray;
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
