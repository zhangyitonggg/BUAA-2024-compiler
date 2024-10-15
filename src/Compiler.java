import Utils.ErrorLog;
import Utils.Printer;
import frontend.lexer.Lexer;
import frontend.lexer.TokenStream;
import frontend.parser.AST.CompUnit;
import frontend.parser.Parser;

import java.io.File;
import java.io.IOException;

public class Compiler {
    private final static String inputFileName = "testfile.txt";
    
    public static void main(String[] args) throws IOException {
        // 词法分析
        File inputFile = new File(inputFileName);
        TokenStream tokenStream = Lexer.getInstance(inputFile).lex();
        Printer.print2lexer(tokenStream.toString());
        // 语法分析
        CompUnit compUnit = Parser.getInstance(tokenStream).parse();
        Printer.print2parser(compUnit.toString());
        // 语义分析
        TableStack.analyse(compUnit);
        // 输出错误
        if (ErrorLog.getInstance().getErrorNum() != 0) {
            System.out.println("error");
            Printer.print2error(ErrorLog.getInstance().toString());
            return;
        }
    }
}
