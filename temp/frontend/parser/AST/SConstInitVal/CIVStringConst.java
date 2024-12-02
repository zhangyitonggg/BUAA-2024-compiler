package frontend.parser.AST.SConstInitVal;

import frontend.lexer.Token;
import frontend.parser.AnyNode;

import java.util.ArrayList;

public class CIVStringConst implements ConstInitVal, AnyNode {
    Token stringConst;
    
    public CIVStringConst(Token stringConst) {
        this.stringConst = stringConst;
    }
    
    
    public ArrayList<Integer> getAsciis() {
        ArrayList<Integer> res = new ArrayList<>();
        String str = this.stringConst.getValue();
        str = str.replaceAll("^\"|\"$", "");
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\') {
                i++;
                c = '\n';
            }
            res.add((int) c);
        }
        return res;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(stringConst.toString());
        sb.append("<ConstInitVal>\n");
        return sb.toString();
    }
}
