package frontend.parser.AST.Exp;

import frontend.parser.AnyNode;

// 常量表达式 ConstExp → AddExp 注：使用的 Ident 必须是常量 // 存在即可
public class ConstExp implements AnyNode {
    private AddExp addExp;
    
    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(addExp.toString());
        sb.append("<ConstExp>\n");
        return sb.toString();
    }
}
