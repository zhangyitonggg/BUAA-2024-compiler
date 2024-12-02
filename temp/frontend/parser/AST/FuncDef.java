package frontend.parser.AST;

import Utils.Printer;
import frontend.checker.symbol.Symbol;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AnyNode;

import java.util.ArrayList;

// 函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
public class FuncDef implements AnyNode {
    private FuncType funcType;
    private Token ident;
    private FuncFParams funcFParams; // 可能为null
    private Block block;
    
    public FuncDef(FuncType funcType, Token ident, FuncFParams funcFParams, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;
    }
    
    public FuncType getFuncType() {
        return funcType;
    }
    
    public Token getIdent() {
        return ident;
    }
    
    public FuncFParams getFuncFParams() {
        return funcFParams;
    }
    
    public Block getBlock() {
        return block;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(funcType.toString());
        sb.append(ident.toString());
        sb.append(Printer.ST(TokenType.LPARENT));
        if (funcFParams != null) {
            sb.append(funcFParams.toString());
        }
        sb.append(Printer.ST(TokenType.RPARENT));
        sb.append(block.toString());
        sb.append("<FuncDef>\n");
        return sb.toString();
    }
}
