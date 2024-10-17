package frontend.parser.AST.Stmt;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.AnyNode;

public class ReturnSubStmt implements Stmt, BlockItem, AnyNode {
    private Exp exp; // 可能为null
    private Token returnToken;
    
    public ReturnSubStmt(Exp exp, Token returnToken) {
        this.exp = exp;
        this.returnToken = returnToken;
    }
    
    public Exp getExp() {
        return exp;
    }
    
    public Token getReturnToken() {
        return returnToken;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Printer.ST(TokenType.RETURNTK));
        if (exp != null) {
            sb.append(exp.toString());
        }
        sb.append(Printer.ST(TokenType.SEMICN));
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
