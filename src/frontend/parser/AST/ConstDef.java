package frontend.parser.AST;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.ConstExp;
import frontend.parser.AST.SConstInitVal.ConstInitVal;

// 常量定义 ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
public class ConstDef {
    private Token ident;
    private ConstExp constExp;
    private ConstInitVal constInitVal;
    
    public ConstDef(Token ident, ConstExp constExp, ConstInitVal constInitVal) {
        this.ident = ident;
        this.constExp = constExp;
        this.constInitVal = constInitVal;
    }
    
    public ConstDef(Token ident, ConstInitVal constInitVal) {
        this.ident = ident;
        this.constExp = null;
        this.constInitVal = constInitVal;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        if (constExp != null) {
            sb.append(Printer.ST(TokenType.LBRACK));
            sb.append(constExp.toString());
            sb.append(Printer.ST(TokenType.RBRACK));
        }
        sb.append(Printer.ST(TokenType.ASSIGN));
        sb.append(constInitVal.toString());
        sb.append("<ConstDef>\n");
        return sb.toString();
    }
}
