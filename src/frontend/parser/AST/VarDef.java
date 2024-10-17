package frontend.parser.AST;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.ConstExp;
import frontend.parser.AST.SInitVal.InitVal;
import frontend.parser.AnyNode;

// 变量定义 VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
public class VarDef implements AnyNode {
    private Token ident;
    private ConstExp constExp; // 可能为null
    private InitVal initVal; // 可能为null
    
    public VarDef(Token ident, ConstExp constExp, InitVal initVal) {
        this.ident = ident;
        this.constExp = constExp;
        this.initVal = initVal;
    }
    
    public Token getIdent() {
        return ident;
    }
    
    public ConstExp getConstExp() {
        return constExp;
    }
    
    public InitVal getInitVal() {
        return initVal;
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
