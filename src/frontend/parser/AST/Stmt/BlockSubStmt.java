package frontend.parser.AST.Stmt;

import frontend.parser.AST.Block;
import frontend.parser.AST.BlockItem;

public class BlockSubStmt implements Stmt, BlockItem {
    private Block block;
    
    public BlockSubStmt(Block block) {
        this.block = block;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(block.toString());
        sb.append("<Stmt>\n");
        return sb.toString();
    }
}
