package frontend.parser.AST.SInitVal;

import frontend.lexer.Token;
import frontend.parser.AST.Exp.Exp;

public class IVStringConst implements InitVal {
    private Token stringConst;
    
    public IVStringConst(Token stringConst) {
        this.stringConst = stringConst;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(stringConst.toString());
        sb.append("<InitVal>\n");
        return sb.toString();
    }
}
