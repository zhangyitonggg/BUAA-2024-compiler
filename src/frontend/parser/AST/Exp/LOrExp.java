package frontend.parser.AST.Exp;

import frontend.lexer.Token;

import java.util.ArrayList;

// 逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp
// 改写为： LOrExp → LAndExp {&& LAndExp}
public class LOrExp {
    private ArrayList<Object> nodes;
    
    public LOrExp(ArrayList<Object> nodes) {
        this.nodes = nodes;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        LAndExp lAndExp = (LAndExp) nodes.get(0);
        sb.append(lAndExp.toString());
        sb.append("<LOrExp>\n");
        for (int i = 1; i < nodes.size(); i += 2) {
            Token token = (Token) nodes.get(i);
            lAndExp = (LAndExp) nodes.get(i + 1);
            sb.append(token.toString());
            sb.append(lAndExp.toString());
            sb.append("<LOrExp>\n");
        }
        return sb.toString();
    }
}