package frontend.parser.AST.Exp;

import frontend.lexer.Token;

// 数值 Number → IntConst
public class Number {
    private Token intConst;
    
    public Number(Token intConst) {
        this.intConst = intConst;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(intConst.toString());
        sb.append("<Number>\n");
        return sb.toString();
    }
}
