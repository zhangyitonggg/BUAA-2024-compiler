package frontend.parser.AST.Stmt;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.AST.Exp.LVal;
import frontend.parser.AnyNode;

public class AssignSubStmt implements Stmt, BlockItem, AnyNode {
    private LVal lVal;
    private Exp exp;
    
    public AssignSubStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal.toString());
        sb.append(Printer.ST(TokenType.ASSIGN));
        sb.append(exp.toString());
        sb.append(Printer.ST(TokenType.SEMICN));
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}