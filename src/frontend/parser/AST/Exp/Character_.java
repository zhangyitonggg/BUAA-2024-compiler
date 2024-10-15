package frontend.parser.AST.Exp;

import frontend.lexer.Token;
import frontend.parser.AnyNode;

// 字符 Character → CharConst
public class Character_ implements AnyNode {
    private Token charConst;
    
    public Character_(Token charConst) {
        this.charConst = charConst;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(charConst.toString());
        sb.append("<Character>\n");
        return sb.toString();
    }
}