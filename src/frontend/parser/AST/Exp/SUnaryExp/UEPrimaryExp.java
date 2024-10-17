package frontend.parser.AST.Exp.SUnaryExp;

import frontend.lexer.Token;
import frontend.parser.AST.Exp.SPrimaryExp.PELVal;
import frontend.parser.AST.Exp.SPrimaryExp.PrimaryExp;
import frontend.parser.AnyNode;

public class UEPrimaryExp implements UnaryExp, AnyNode {
    private PrimaryExp primaryExp;
    
    public UEPrimaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }
    
    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(primaryExp.toString());
        sb.append("<UnaryExp>\n");
        return sb.toString();
    }
    
    public Token tryGetIdent() {
        if (primaryExp instanceof PELVal) {
            return ((PELVal) primaryExp).tryGetIdent();
        }
        return null;
    }
}
