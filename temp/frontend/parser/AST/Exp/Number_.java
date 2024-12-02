package frontend.parser.AST.Exp;

import frontend.lexer.Token;
import frontend.parser.AnyNode;

// 数值 Number → IntConst
public class Number_ implements AnyNode {
    private Token intConst;
    
    public Number_(Token intConst) {
        this.intConst = intConst;
    }
    
    public int getNumber() {
        return Integer.parseInt(intConst.getValue());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(intConst.toString());
        sb.append("<Number>\n");
        return sb.toString();
    }
}
