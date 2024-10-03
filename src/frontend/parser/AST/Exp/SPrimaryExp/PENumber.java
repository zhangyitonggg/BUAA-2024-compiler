package frontend.parser.AST.Exp.SPrimaryExp;

import frontend.parser.AST.Exp.Number_;

public class PENumber implements PrimaryExp {
    private Number_ number;
    
    public PENumber(Number_ number) {
        this.number = number;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(number.toString());
        sb.append("<PrimaryExp>\n");
        return sb.toString();
    }
}
