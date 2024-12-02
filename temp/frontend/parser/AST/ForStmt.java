package frontend.parser.AST;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.AST.Exp.LVal;
import frontend.parser.AnyNode;

// 语句 ForStmt → LVal '=' Exp
public class ForStmt implements AnyNode {
    private LVal lVal;
    private Exp exp;
    
    public ForStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }
    
    public LVal getLVal() {
        return lVal;
    }
    
    public Exp getExp() {
        return exp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal.toString());
        sb.append(Printer.ST(TokenType.ASSIGN));
        sb.append(exp.toString());
        sb.append("<ForStmt>\n");
        return sb.toString();
    }
}
