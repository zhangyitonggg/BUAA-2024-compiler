package frontend.parser.AST.Exp;

// 表达式 Exp → AddExp
public class Exp {
    private AddExp addExp;
    
    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(addExp.toString());
        sb.append("<Exp>\n");
        return sb.toString();
    }
}
