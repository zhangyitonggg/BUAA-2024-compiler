package frontend.parser.AST;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.Exp;

import java.util.ArrayList;

// 变量初值 InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
public class InitVal {
    /*
     * 0 -> Exp
     * 1 -> '{' [ Exp { ',' Exp } ] '}'
     * 2 -> StringConst
     * */
    private int type;
    private ArrayList<Exp> exps;
    private Token stringConst;
    
    public InitVal(int type, ArrayList<Exp> exps) {
        this.type = type;
        this.exps = exps;
        this.stringConst = null;
    }
    
    public InitVal(int type, Token stringConst) {
        this.type = type;
        this.exps = new ArrayList<>();
        this.stringConst = stringConst;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch(this.type) {
            case 0 -> sb.append(exps.get(0).toString());
            case 1 -> {
                sb.append(Printer.ST(TokenType.LBRACE));
                boolean flag = false;
                for(Exp exp : exps) {
                    if (flag) {
                        sb.append(Printer.ST(TokenType.COMMA));
                        sb.append(exp.toString());
                    } else {
                        sb.append(exp.toString());
                        flag = true;
                    }
                }
                sb.append(Printer.ST(TokenType.RBRACE));
            }
            case 2 -> {
                sb.append(stringConst.toString());
            }
        }
        sb.append("<InitVal>\n");
        return sb.toString();
    }
}