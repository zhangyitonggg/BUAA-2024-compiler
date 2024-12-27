package frontend.parser.AST.Stmt;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.BlockItem;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.AnyNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrintfSubStmt implements Stmt, BlockItem, AnyNode {
    private Token printfToken;
    private Token stringConst;
    private ArrayList<Exp> exps; // 不能为null，但可以空
    
    public PrintfSubStmt(Token printfToken, Token stringConst, ArrayList<Exp> exps) {
        this.printfToken = printfToken;
        this.stringConst = stringConst;
        this.exps = exps;
    }
    
    public Token getPrintfToken() {
        return printfToken;
    }
    
    public Token getStringConst() {
        return stringConst;
    }
    
    public ArrayList<Exp> getExps() {
        return exps;
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
    
    // 返回的值中去掉了首尾的分号
    public ArrayList<String> splitStringConst() {
        String input = stringConst.getValue();
        input = input.replaceAll("^\"|\"$", "");
        ArrayList<String> result = new ArrayList<>();
        List<String> placeholders = Arrays.asList("%d", "%c");
        int index = 0;
        
        while (index < input.length()) {
            int nearestIndex = -1;
            String nearestPlaceholder = null;
            
            // 找到距离当前索引最近的占位符及其位置
            for (String placeholder : placeholders) {
                int placeholderIndex = input.indexOf(placeholder, index);
                if (placeholderIndex != -1 && (nearestIndex == -1 || placeholderIndex < nearestIndex)) {
                    nearestIndex = placeholderIndex;
                    nearestPlaceholder = placeholder;
                }
            }
            
            // 如果没有找到占位符，直接添加剩余的子串并退出循环
            if (nearestIndex == -1) {
                result.add(input.substring(index));
                break;
            }
            
            // 添加占位符之前的子串（如果有）
            if (nearestIndex > index) {
                result.add(input.substring(index, nearestIndex));
            }
            
            // 添加占位符
            result.add(nearestPlaceholder);
            
            // 更新索引，跳过当前占位符
            index = nearestIndex + nearestPlaceholder.length();
        }
        
        return result;
    }
}
