package frontend.parser.AST.SConstInitVal;

import frontend.parser.AST.Exp.ConstExp;
import frontend.parser.AnyNode;

public class CIVConstExp implements ConstInitVal, AnyNode {
    private ConstExp constExp;
    
    public CIVConstExp(ConstExp constExp) {
        this.constExp = constExp;
    }
    
    public ConstExp getConstExp() {
        return this.constExp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(constExp.toString());
        sb.append("<ConstInitVal>\n");
        return sb.toString();
    }
}
