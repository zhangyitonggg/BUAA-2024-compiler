package frontend.parser.AST.Exp.SUnaryExp;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.FuncRParams;
import frontend.parser.AnyNode;

public class UEFunc implements UnaryExp, AnyNode {
    private Token ident;
    private FuncRParams funcRParams; // 可能为null
    
    public UEFunc(Token ident, FuncRParams funcRParams) {
        this.ident = ident;
        this.funcRParams = funcRParams;
    }
    
    public Token getIdent() {
        return ident;
    }
    
    public FuncRParams getFuncRParams() {
        return funcRParams;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.toString());
        sb.append(Printer.ST(TokenType.LPARENT));
        if (funcRParams != null) {
            sb.append(funcRParams.toString());
        }
        sb.append(Printer.ST(TokenType.RPARENT));
        sb.append("<UnaryExp>\n");
        return sb.toString();
    }
}
