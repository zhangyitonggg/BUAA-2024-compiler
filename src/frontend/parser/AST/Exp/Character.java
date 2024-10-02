package frontend.parser.AST.Exp;

import frontend.lexer.Token;

// 字符 Character → CharConst
public class Character {
    private Token charConst;
    
    public Character(Token charConst) {
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
