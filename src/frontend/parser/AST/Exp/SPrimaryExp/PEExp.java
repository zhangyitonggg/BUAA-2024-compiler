package frontend.parser.AST.Exp.SPrimaryExp;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.AnyNode;

public class PEExp implements PrimaryExp, AnyNode {
    private Exp exp;
    
    public PEExp(Exp exp) {
        this.exp = exp;
    }
    
    public Exp getExp() {
        return exp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Printer.ST(TokenType.LPARENT));
        sb.append(exp.toString());
        sb.append(Printer.ST(TokenType.RPARENT));
        sb.append("<PrimaryExp>\n");
        return sb.toString();
    }
}