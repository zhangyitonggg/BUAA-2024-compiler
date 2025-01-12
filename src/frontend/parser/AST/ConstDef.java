package frontend.parser.AST;

import Utils.Printer;
import frontend.checker.symbol.Symbol;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.ConstExp;
import frontend.parser.AST.SConstInitVal.ConstInitVal;
import frontend.parser.AnyNode;

// 常量定义 ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
public class ConstDef implements AnyNode {
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
    
    public Token getIdent() {
        return this.ident;
    }
    
    public ConstExp getConstExp() {
        return constExp;
    }
    
    public ConstInitVal getConstInitVal() {
        return constInitVal;
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
