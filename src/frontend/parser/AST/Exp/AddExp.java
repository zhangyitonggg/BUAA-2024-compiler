package frontend.parser.AST.Exp;

import frontend.lexer.Token;

import java.util.ArrayList;

// 加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp
// 改写为： AddExp → MulExp {('+' | '−') MulExp}
public class AddExp {
    private ArrayList<Object> nodes;
    
    public AddExp(ArrayList<Object> nodes) {
        this.nodes = nodes;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        MulExp mulExp = (MulExp) nodes.get(0);
        sb.append(mulExp.toString());
        sb.append("<AddExp>\n");
        for (int i = 1; i < nodes.size(); i += 2) {
            Token token = (Token) nodes.get(i);
            mulExp = (MulExp) nodes.get(i + 1);
            sb.append(token.toString());
            sb.append(mulExp.toString());
            sb.append("<AddExp>\n");
        }
        return sb.toString();
    }
}