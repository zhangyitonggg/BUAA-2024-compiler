package frontend.parser.AST.Exp;

import frontend.lexer.Token;
import frontend.parser.AnyNode;

import java.util.ArrayList;

// 相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
// 改写为： EqExp → RelExp {('==' | '!=') RelExp}
public class EqExp implements AnyNode {
    private ArrayList<Object> nodes;
    
    public EqExp(ArrayList<Object> nodes) {
        this.nodes = nodes;
    }
    
    public ArrayList<Object> getNodes() {
        return nodes;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        RelExp relExp = (RelExp) nodes.get(0);
        sb.append(relExp.toString());
        sb.append("<EqExp>\n");
        for (int i = 1; i < nodes.size(); i += 2) {
            Token token = (Token) nodes.get(i);
            relExp = (RelExp) nodes.get(i + 1);
            sb.append(token.toString());
            sb.append(relExp.toString());
            sb.append("<EqExp>\n");
        }
        return sb.toString();
    }
}