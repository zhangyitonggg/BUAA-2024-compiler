package frontend.parser.AST.Exp.SPrimaryExp;

import frontend.parser.AST.Exp.LVal;

public class PELVal implements PrimaryExp {
    private LVal lVal;
    
    public PELVal(LVal lVal) {
        this.lVal = lVal;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal.toString());
        sb.append("<PrimaryExp>\n");
        return sb.toString();
    }
}
