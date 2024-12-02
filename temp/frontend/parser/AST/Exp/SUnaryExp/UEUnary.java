package frontend.parser.AST.Exp.SUnaryExp;

import frontend.parser.AnyNode;

public class UEUnary implements UnaryExp, AnyNode {
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;
    
    public UEUnary(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }
    
    public UnaryOp getUnaryOp() {
        return unaryOp;
    }
    
    public UnaryExp getUnaryExp() {
        return unaryExp;
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