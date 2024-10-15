package frontend.parser.AST.Exp.SUnaryExp;

import frontend.parser.AST.Exp.SPrimaryExp.PrimaryExp;
import frontend.parser.AnyNode;

public class UEPrimaryExp implements UnaryExp, AnyNode {
    private PrimaryExp primaryExp;
    
    public UEPrimaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(primaryExp.toString());
        sb.append("<UnaryExp>\n");
        return sb.toString();
    }
}
