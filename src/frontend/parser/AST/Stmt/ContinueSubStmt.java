package frontend.parser.AST.Stmt;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.BlockItem;
import frontend.parser.AnyNode;

public class ContinueSubStmt implements Stmt, BlockItem, AnyNode {
    private Token continueToken;
    
    public ContinueSubStmt(Token continueToken) {
        this.continueToken = continueToken;
    }
    
    public Token getContinueToken() {
        return continueToken;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Printer.ST(TokenType.CONTINUETK));
        sb.append(Printer.ST(TokenType.SEMICN));
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
