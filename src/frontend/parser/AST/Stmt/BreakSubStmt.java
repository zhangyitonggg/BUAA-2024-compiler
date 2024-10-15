package frontend.parser.AST.Stmt;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AST.BlockItem;
import frontend.parser.AnyNode;

public class BreakSubStmt implements Stmt, BlockItem, AnyNode {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Printer.ST(TokenType.BREAKTK));
        sb.append(Printer.ST(TokenType.SEMICN));
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}