package frontend.parser.AST.Exp;

import frontend.lexer.Token;
import frontend.parser.AnyNode;

// 表达式 Exp → AddExp
public class Exp implements AnyNode {
    private AddExp addExp;
    
    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }
    
    public AddExp getAddExp() {
        return addExp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(addExp.toString());
        sb.append("<Exp>\n");
        return sb.toString();
    }
    
    public Token tryGetIdent() {
        return addExp.tryGetIdent();
    }
}
