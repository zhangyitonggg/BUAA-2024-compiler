package frontend.parser.AST.Exp;

import frontend.lexer.Token;
import frontend.parser.AST.Exp.SUnaryExp.UEPrimaryExp;
import frontend.parser.AST.Exp.SUnaryExp.UnaryExp;
import frontend.parser.AnyNode;

import java.util.ArrayList;

// 乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
// 改写为： MulExp → UnaryExp {('*' | '/' | '%') UnaryExp}
public class MulExp implements AnyNode {
    private ArrayList<Object> nodes;
    
    public MulExp(ArrayList<Object> nodes) {
        this.nodes = nodes;
    }
    
    public ArrayList<Object> getNodes() {
        return nodes;
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
    
    public Token tryGetIdent() {
        if (nodes.size() != 1) {
            return null;
        }
        if (nodes.get(0) instanceof UEPrimaryExp) {
            return ((UEPrimaryExp) nodes.get(0)).tryGetIdent();
        }
        return null;
    }
}
