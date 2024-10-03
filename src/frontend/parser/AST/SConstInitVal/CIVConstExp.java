package frontend.parser.AST.SConstInitVal;

import frontend.parser.AST.Exp.ConstExp;

public class CIVConstExp implements ConstInitVal {
    private ConstExp constExp;
    
    public CIVConstExp(ConstExp constExp) {
        this.constExp = constExp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(constExp.toString());
        sb.append("<ConstInitVal>\n");
        return sb.toString();
    }
}
