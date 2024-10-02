package frontend.parser.AST.Stmt;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.Cond;
import frontend.parser.AST.ForStmt;

public class ForSubStmt implements Stmt, BlockItem {
    private ForStmt lForStmt;
    private Cond cond;
    private ForStmt rForStmt;
    private Stmt stmt;
    
    public ForSubStmt(ForStmt lForStmt, Cond cond, ForStmt rForStmt, Stmt stmt) {
        this.lForStmt = lForStmt;
        this.cond = cond;
        this.rForStmt = rForStmt;
        this.stmt = stmt;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Printer.ST(TokenType.FORTK));
        sb.append(Printer.ST(TokenType.LPARENT));
        if (lForStmt != null) {
            sb.append(lForStmt.toString());
        }
        sb.append(Printer.ST(TokenType.SEMICN));
        if (cond != null) {
            sb.append(cond.toString());
        }
        sb.append(Printer.ST(TokenType.SEMICN));
        if (rForStmt != null) {
            sb.append(rForStmt.toString());
        }
        sb.append(Printer.ST(TokenType.RPARENT));
        sb.append(stmt.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
