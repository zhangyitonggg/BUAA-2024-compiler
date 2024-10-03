package frontend.parser.AST.Stmt;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.Exp;

import java.util.ArrayList;

public class PrintfSubStmt implements Stmt, BlockItem {
    private Token stringConst;
    private ArrayList<Exp> exps; // 不能为null，但可以空
    
    public PrintfSubStmt(Token stringConst, ArrayList<Exp> exps) {
        this.stringConst = stringConst;
        this.exps = exps;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Printer.ST(TokenType.PRINTFTK));
        sb.append(Printer.ST(TokenType.LPARENT));
        sb.append(stringConst.toString());
        for(Exp exp : exps) {
            sb.append(Printer.ST(TokenType.COMMA));
            sb.append(exp.toString());
        }
        sb.append(Printer.ST(TokenType.RPARENT));
        sb.append(Printer.ST(TokenType.SEMICN));
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
