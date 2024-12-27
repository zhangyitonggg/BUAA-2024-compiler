package frontend.parser.AST;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AnyNode;

import java.util.ArrayList;

// 常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
public class ConstDecl implements Decl, BlockItem, AnyNode {
    private Token bType; // int or char
    private ArrayList<ConstDef> constDefs;
    
    public ConstDecl(Token bType, ArrayList<ConstDef> constDefs) {
        this.bType = bType;
        this.constDefs = constDefs;
    }
    
    public Token getBType() {
        return bType;
    }
    
    public ArrayList<ConstDef> getConstDefs() {
        return constDefs;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Printer.ST(TokenType.CONSTTK));
        sb.append(bType.toString());
        boolean flag = false;
        for(ConstDef constDef : constDefs) {
            if (flag) {
                sb.append(Printer.ST(TokenType.COMMA));
                sb.append(constDef.toString());
            } else {
                sb.append(constDef.toString());
                flag = true;
            }
        }
        sb.append(Printer.ST(TokenType.SEMICN));
        sb.append("<ConstDecl>\n");
        return sb.toString();
    }
}
