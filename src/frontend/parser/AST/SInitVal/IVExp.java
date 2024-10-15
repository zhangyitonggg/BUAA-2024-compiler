package frontend.parser.AST.SInitVal;

import frontend.parser.AST.Exp.Exp;
import frontend.parser.AnyNode;

public class IVExp implements InitVal, AnyNode {
    private Exp exp;
    
    public IVExp(Exp exp) {
        this.exp = exp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(exp.toString());
        sb.append("<InitVal>\n");
        return sb.toString();
    }
}
