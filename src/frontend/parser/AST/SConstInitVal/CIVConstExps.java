package frontend.parser.AST.SConstInitVal;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.ConstExp;
import frontend.parser.AnyNode;

import java.util.ArrayList;

public class CIVConstExps implements ConstInitVal, AnyNode {
    private ArrayList<ConstExp> constExps;
    
    public CIVConstExps(ArrayList<ConstExp> constExps) {
        this.constExps = constExps;
    }
    
    public ArrayList<ConstExp> getConstExps() {
        return constExps;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Printer.ST(TokenType.LBRACE));
        boolean flag = false;
        for(ConstExp constExp : constExps) {
            if (flag) {
                sb.append(Printer.ST(TokenType.COMMA));
                sb.append(constExp.toString());
            } else {
                sb.append(constExp.toString());
                flag = true;
            }
        }
        sb.append(Printer.ST(TokenType.RBRACE));
        sb.append("<ConstInitVal>\n");
        return sb.toString();
    }
}
