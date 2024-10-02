package frontend.parser.AST.Stmt;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.LVal;

public class GetcharSubStmt implements Stmt, BlockItem {
    private LVal lVal;
    
    public GetcharSubStmt(LVal lVal){
        this.lVal = lVal;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lVal.toString());
        sb.append(Printer.ST(TokenType.ASSIGN));
        sb.append(Printer.ST(TokenType.GETCHARTK));
        sb.append(Printer.ST(TokenType.LPARENT));
        sb.append(Printer.ST(TokenType.RPARENT));
        sb.append(Printer.ST(TokenType.SEMICN));
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
