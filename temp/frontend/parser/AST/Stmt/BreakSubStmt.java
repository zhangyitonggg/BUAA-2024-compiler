package frontend.parser.AST.Stmt;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.BlockItem;
import frontend.parser.AnyNode;

public class BreakSubStmt implements Stmt, BlockItem, AnyNode {
    private Token breakToken;
    
    public BreakSubStmt(Token breakToken) {
        this.breakToken = breakToken;
    }
    
    public Token getBreakToken() {
        return breakToken;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Printer.ST(TokenType.BREAKTK));
        sb.append(Printer.ST(TokenType.SEMICN));
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
