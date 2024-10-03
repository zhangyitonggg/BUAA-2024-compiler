package frontend.parser.AST.Exp.SUnaryExp;

public class UEUnary implements UnaryExp{
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;
    
    public UEUnary(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(unaryOp.toString());
        sb.append(unaryExp.toString());
        sb.append("<UnaryExp>\n");
        return sb.toString();
    }
}