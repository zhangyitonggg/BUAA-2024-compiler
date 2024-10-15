package frontend.parser.AST.Exp;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AnyNode;

import java.util.ArrayList;

public class FuncRParams implements AnyNode {
    private ArrayList<Exp> exps;
    
    public FuncRParams(ArrayList<Exp> exps) {
        this.exps = exps;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        for (Exp exp : exps) {
            if (flag) {
                sb.append(Printer.ST(TokenType.COMMA));
                sb.append(exp.toString());
            } else {
                sb.append(exp.toString());
                flag = true;
            }
        }
        sb.append("<FuncRParams>\n");
        return sb.toString();
    }
}
