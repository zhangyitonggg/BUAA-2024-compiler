package frontend.parser.AST;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AnyNode;

import java.util.ArrayList;

// 语句块 Block → '{' { BlockItem } '}'
public class Block implements AnyNode {
    private ArrayList<BlockItem> blockItems;
    
    public Block(ArrayList<BlockItem> blockItems) {
        this.blockItems = blockItems;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Printer.ST(TokenType.LBRACE));
        for (BlockItem blockItem : blockItems) {
            sb.append(blockItem.toString());
        }
        sb.append(Printer.ST(TokenType.RBRACE));
        sb.append("<Block>\n");
        return sb.toString();
    }
}
