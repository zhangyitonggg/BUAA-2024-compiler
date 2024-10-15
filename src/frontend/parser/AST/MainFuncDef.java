package frontend.parser.AST;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AnyNode;

// 主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block
public class MainFuncDef implements AnyNode {
    private Block block;
    
    public MainFuncDef(Block block) {
        this.block = block;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Printer.ST(TokenType.INTTK));
        sb.append(Printer.ST(TokenType.MAINTK));
        sb.append(Printer.ST(TokenType.LPARENT));
        sb.append(Printer.ST(TokenType.RPARENT));
        sb.append(block.toString());
        sb.append("<MainFuncDef>\n");
        return sb.toString();
    }
}
