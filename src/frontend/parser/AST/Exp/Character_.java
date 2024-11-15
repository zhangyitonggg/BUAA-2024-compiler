package frontend.parser.AST.Exp;

import frontend.lexer.Token;
import frontend.parser.AnyNode;

// 字符 Character → CharConst
public class Character_ implements AnyNode {
    private Token charConst;
    
    public Character_(Token charConst) {
        this.charConst = charConst;
    }
    
    public int getAscii() {
        String str = charConst.getValue();
        
        // 1. 去掉两端的单引号（或者其他包围字符）
        str = str.replaceAll("^'|'$", "");  // 使用正则去掉字符串两端的单引号
        
        // 2. 获取字符并转化为对应的 ASCII 码
        char c = str.charAt(0);
        int asciiValue;
        if (c == '\\') {
            c = str.charAt(1);
            switch (c) {
                case 'a' -> asciiValue = 7;
                case 'b' -> asciiValue = 8;
                case 't' -> asciiValue = 9;
                case 'n' -> asciiValue = 10;
                case 'v' -> asciiValue = 11;
                case 'f' -> asciiValue = 12;
                case '\"' -> asciiValue = 34;
                case '\'' -> asciiValue = 39;
                case '\\' -> asciiValue = 92;
                case '0' -> asciiValue = 0;
                default -> throw new IllegalArgumentException("Unknown escape sequence: \\" + c);
            }
        } else {
            asciiValue = (int) c;
        }
        return asciiValue;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(charConst.toString());
        sb.append("<Character>\n");
        return sb.toString();
    }
}
