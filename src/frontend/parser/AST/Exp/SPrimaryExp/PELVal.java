package frontend.parser.AST.Exp.SPrimaryExp;

import frontend.lexer.Token;
import frontend.parser.AST.Exp.LVal;
import frontend.parser.AnyNode;

public class PELVal implements PrimaryExp, AnyNode {
    private LVal lVal;
    
    public PELVal(LVal lVal) {
        this.lVal = lVal;
    }
    
    public LVal getlVal() {
        return lVal;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal.toString());
        sb.append("<PrimaryExp>\n");
        return sb.toString();
    }
    
    public Token tryGetIdent() {
        return lVal.tryGetIdent();
    }
}
