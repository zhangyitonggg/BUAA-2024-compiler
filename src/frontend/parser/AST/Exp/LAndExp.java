package frontend.parser.AST.Exp;

import frontend.lexer.Token;
import frontend.parser.AnyNode;

import java.util.ArrayList;

// 逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp
// 改写为： LAndExp → EqExp {&& EqExp}
public class LAndExp implements AnyNode {
    private ArrayList<Object> nodes;
    
    public LAndExp(ArrayList<Object> nodes) {
        this.nodes = nodes;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        EqExp eqExp = (EqExp) nodes.get(0);
        sb.append(eqExp.toString());
        sb.append("<LAndExp>\n");
        for (int i = 1; i < nodes.size(); i += 2) {
            Token token = (Token) nodes.get(i);
            eqExp = (EqExp) nodes.get(i + 1);
            sb.append(token.toString());
            sb.append(eqExp.toString());
            sb.append("<LAndExp>\n");
        }
        return sb.toString();
    }
}