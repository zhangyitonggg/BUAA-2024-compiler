package frontend.parser.AST;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.ConstExp;

// 变量定义 VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
public class VarDef {
    private Token ident;
    private ConstExp constExp; // 可能为null
    private InitVal initVal; // 可能为null
    
    public VarDef(Token ident, ConstExp constExp, InitVal initVal) {
        this.ident = ident;
        this.constExp = constExp;
        this.initVal = initVal;
    }
    
    public VarDef(Token ident, ConstExp constExp) {
        this.ident = ident;
        this.constExp = constExp;
        this.initVal = null;
    }
    
    public VarDef(Token ident, InitVal initVal) {
        this.ident = ident;
        this.constExp = null;
        this.initVal = initVal;
    }
    
    public VarDef(Token ident) {
        this.ident = ident;
        this.constExp = null;
        this.initVal = null;
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
        if (initVal != null) {
            sb.append(Printer.ST(TokenType.ASSIGN));
            sb.append(initVal.toString());
        }
        sb.append("<VarDef>\n");
        return sb.toString();
    }
}
