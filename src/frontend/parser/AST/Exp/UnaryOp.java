package frontend.parser.AST.Exp;

import frontend.lexer.Token;

// 单目运算符 UnaryOp → '+' | '−' | '!'   注：'!'仅出现在条件表达式中
public class UnaryOp {
    private Token op;
    
    public UnaryOp(Token op) {
        this.op = op;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(op.toString());
        sb.append("<UnaryOp>\n");
        return sb.toString();
    }
}
