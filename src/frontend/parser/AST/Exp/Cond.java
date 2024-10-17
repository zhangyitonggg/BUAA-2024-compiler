package frontend.parser.AST.Exp;

import frontend.parser.AnyNode;

// 条件表达式 Cond → LOrExp
public class Cond implements AnyNode {
    private LOrExp lOrExp;
    
    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }
    
    public LOrExp getLOrExp() {
        return lOrExp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lOrExp.toString());
        sb.append("<Cond>\n");
        return sb.toString();
    }
}
