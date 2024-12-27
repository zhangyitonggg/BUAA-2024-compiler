package frontend.parser.AST.Exp;

import frontend.lexer.Token;
import frontend.parser.AnyNode;

import java.util.ArrayList;

// 关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
// 改写为： RelExp → AddExp {('<' | '>' | '<=' | '>=') AddExp}
public class RelExp implements AnyNode {
    private ArrayList<Object> nodes;
    
    public RelExp(ArrayList<Object> nodes) {
        this.nodes = nodes;
    }
    
    public ArrayList<Object> getNodes() {
        return nodes;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        AddExp addExp = (AddExp) nodes.get(0);
        sb.append(addExp.toString());
        sb.append("<RelExp>\n");
        for (int i = 1; i < nodes.size(); i += 2) {
            Token token = (Token) nodes.get(i);
            addExp = (AddExp) nodes.get(i + 1);
            sb.append(token.toString());
            sb.append(addExp.toString());
            sb.append("<RelExp>\n");
        }
        return sb.toString();
    }
}