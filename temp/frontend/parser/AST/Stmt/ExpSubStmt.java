package frontend.parser.AST.Stmt;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.AnyNode;

public class ExpSubStmt implements Stmt, BlockItem, AnyNode {
    private Exp exp; // 可能为null
    
    public ExpSubStmt(Exp exp) {
        this.exp = exp;
    }
    
    public Exp getExp() {
        return exp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (exp != null) {
            sb.append(exp.toString());
        }
        sb.append(Printer.ST(TokenType.SEMICN));
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
