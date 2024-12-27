package frontend.parser.AST.SInitVal;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.AnyNode;

import java.util.ArrayList;

public class IVExps implements InitVal, AnyNode {
    private ArrayList<Exp> exps;
    
    public IVExps(ArrayList<Exp> exps) {
        this.exps = exps;
    }
    
    public ArrayList<Exp> getExps() {
        return exps;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Printer.ST(TokenType.LBRACE));
        boolean flag = false;
        for(Exp exp : exps) {
            if (flag) {
                sb.append(Printer.ST(TokenType.COMMA));
                sb.append(exp.toString());
            } else {
                sb.append(exp.toString());
                flag = true;
            }
        }
        sb.append(Printer.ST(TokenType.RBRACE));
        sb.append("<InitVal>\n");
        return sb.toString();
    }
}
