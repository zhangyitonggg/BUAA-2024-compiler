package frontend.parser.AST.Stmt;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.Cond;

public class IfSubStmt implements Stmt, BlockItem {
    private Cond cond;
    private Stmt ifStmt;
    private Stmt elStmt; // 可能为null
    
    public IfSubStmt(Cond cond, Stmt ifStmt, Stmt elStmt) {
        this.cond = cond;
        this.ifStmt = ifStmt;
        this.elStmt = elStmt;
    }
    
    public IfSubStmt(Cond cond, Stmt ifStmt) {
        this.cond = cond;
        this.ifStmt = ifStmt;
        this.elStmt = null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(Printer.ST(TokenType.IFTK));
        sb.append(Printer.ST(TokenType.LPARENT));
        sb.append(cond.toString());
        sb.append(Printer.ST(TokenType.RPARENT));
        sb.append(ifStmt.toString());
        if (elStmt != null) {
            sb.append(Printer.ST(TokenType.ELSETK));
            sb.append(elStmt.toString());
        }
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
