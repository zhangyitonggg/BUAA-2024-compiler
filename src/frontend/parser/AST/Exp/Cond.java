package frontend.parser.AST.Exp;

// 条件表达式 Cond → LOrExp
public class Cond {
    private LOrExp lOrExp;
    
    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lOrExp.toString());
        sb.append("<Cond>\n");
        return sb.toString();
    }
}
