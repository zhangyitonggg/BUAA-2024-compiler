import Utils.ErrorLog;
import Utils.Printer;
import frontend.lexer.Lexer;
import frontend.lexer.TokenStream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Compiler {
    private final static String inputFileName = "testfile.txt";
    
    public static void main(String[] args) throws IOException {
        // 词法分析
        File inputFile = new File(inputFileName);
        Lexer lexer = Lexer.getInstance(inputFile);
        TokenStream tokenStream = lexer.lex();
        Printer.print2lexer(tokenStream.toString());
        
        // 语法分析
        
        
        // 输出错误
        if (ErrorLog.getInstance().getErrorNum() != 0) {
            Printer.print2error(ErrorLog.getInstance().toString());
            return;
        }
    }
}
