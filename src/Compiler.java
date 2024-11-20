import Utils.ErrorLog;
import Utils.Printer;
import backend.Mapper;
import frontend.checker.Checker;
import frontend.checker.SymbolTable;
import frontend.lexer.Lexer;
import frontend.lexer.TokenStream;
import frontend.parser.AST.CompUnit;
import frontend.parser.Parser;
import llvm.Module;
import llvm.Visitor;

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
        SymbolTable rootTable = Checker.getInstance().check(compUnit);
        Printer.print2symbol(rootTable.toString());
        // 输出错误
        if (ErrorLog.getInstance().getErrorNum() != 0) {
            System.out.println("error");
            Printer.print2error(ErrorLog.getInstance().toString());
            return;
        }
        // 生成中间代码
        Module module = Visitor.getInstance().visit(compUnit);
        Printer.print2llvm(module.toString());
        // 生成目标代码
        String mipsCode = Mapper.getInstance().map(module);
        Printer.print2mips(mipsCode);
    }
}
