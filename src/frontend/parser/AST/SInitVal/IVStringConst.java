package frontend.parser.AST.SInitVal;

import frontend.lexer.Token;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.AnyNode;
import llvm.types.DataIrTy;
import llvm.value.constant.ConstData;

import java.util.ArrayList;

public class IVStringConst implements InitVal, AnyNode {
    private Token stringConst;
    
    public IVStringConst(Token stringConst) {
        this.stringConst = stringConst;
    }
    
    public Token getStringConst() {
        return this.stringConst;
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
        sb.append("<InitVal>\n");
        return sb.toString();
    }
}
