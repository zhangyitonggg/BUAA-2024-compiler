package llvm.value.notInstr;

import llvm.types.ArrayIrTy;
import llvm.types.DataIrTy;
import llvm.types.IrTy;
import llvm.types.PointerIrTy;
import llvm.value.Value;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringLiteral extends Value {
    private String oriLiteral;
    private String literal;
    
    /**
     * 转义字符中只会出现 '\n'。
     * @param strCount
     * @param literal
     */
    public StringLiteral(int strCount, String literal) {
//        super("@.str." + strCount, new PointerIrTy(calcBaseType(literal)));
        super(null, "@str." + strCount, new PointerIrTy(calcBaseType(literal)));
        this.literal = deleteEnter(literal);
        this.oriLiteral = literal;
    }

    public String getOriLiteral() {
        return oriLiteral;
    }
    
    @Override
    public String toString() {
        return getName() + " = constant " + ((PointerIrTy) getType()).deRefIrTy + " c\"" + literal + "\\00\"";
    }
    
    ///////////////////////////////////////////////////////////////////////////
    private static IrTy calcBaseType(String literal) {
        String delete = deleteEnter(literal);
        int enterNum = delete.length() - literal.length();
        int length = literal.length() - enterNum;
        
        return new ArrayIrTy(DataIrTy.I8, length + 1);
    }
    
    private static String deleteEnter(String input) {
        String replacement = "\\\\0A"; // 替换成的字符串
        
        // 使用正则表达式查找并替换
        Pattern pattern = Pattern.compile("\\\\n"); // 使用正则表达式，匹配 "\\n"
        Matcher matcher = pattern.matcher(input);
        
        // 计数替换次数
        StringBuilder modifiedString = new StringBuilder();
        
        // 使用 find() 方法查找所有匹配并替换
        while (matcher.find()) {
            matcher.appendReplacement(modifiedString, replacement);
        }
        matcher.appendTail(modifiedString); // 把剩余部分加上
        
        return modifiedString.toString();
    }
}
