package frontend.parser.AST;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AnyNode;

import java.util.ArrayList;

// 变量声明 VarDecl → BType VarDef { ',' VarDef } ';'
public class VarDecl implements Decl, BlockItem, AnyNode {
    private Token bType;
    private ArrayList<VarDef> varDefs;
    
    public VarDecl(Token bType, ArrayList<VarDef> varDefs){
        this.bType = bType;
        this.varDefs = varDefs;
    }
    
    public Token getBType() {
        return bType;
    }
    
    public ArrayList<VarDef> getVarDefs() {
        return varDefs;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bType.toString());
        boolean flag = false;
        for(VarDef varDef : varDefs) {
            if (flag) {
                sb.append(Printer.ST(TokenType.COMMA));
                sb.append(varDef.toString());
            } else {
                sb.append(varDef.toString());
                flag = true;
            }
        }
        sb.append(Printer.ST(TokenType.SEMICN));
        sb.append("<VarDecl>\n");
        return sb.toString();
    }
}
