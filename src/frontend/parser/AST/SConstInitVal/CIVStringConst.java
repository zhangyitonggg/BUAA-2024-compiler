package frontend.parser.AST.SConstInitVal;

import frontend.lexer.Token;
import frontend.parser.AnyNode;

public class CIVStringConst implements ConstInitVal, AnyNode {
    Token stringConst;
    
    public CIVStringConst(Token stringConst) {
        this.stringConst = stringConst;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(stringConst.toString());
        sb.append("<ConstInitVal>\n");
        return sb.toString();
    }
}
