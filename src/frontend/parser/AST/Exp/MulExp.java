package frontend.parser.AST.Exp;

import frontend.lexer.Token;

import java.util.ArrayList;

// 乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
// 改写为： MulExp → UnaryExp {('*' | '/' | '%') UnaryExp}
public class MulExp {
    private ArrayList<Object> nodes;
    
    public MulExp(ArrayList<Object> nodes) {
        this.nodes = nodes;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        UnaryExp unaryExp = (UnaryExp) nodes.get(0);
        sb.append(unaryExp.toString());
        sb.append("<MulExp>\n");
        for (int i = 1; i < nodes.size(); i+=2) {
            Token token = (Token) nodes.get(i);
            unaryExp = (UnaryExp) nodes.get(i + 1);
            sb.append(token.toString());
            sb.append(unaryExp.toString());
            sb.append("<MulExp>\n");
        }
        return sb.toString();
    }
}
