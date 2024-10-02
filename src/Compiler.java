import Utils.ErrorLog;
import frontend.lexer.Lexer;
import frontend.lexer.TokenStream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Compiler {
    private final static String inputFileName = "testfile.txt";
    private final static String outputFileName = "lexer.txt";
    private final static String errorFileName = "error.txt";
    
    public static void main(String[] args) throws IOException {
        File inputFile = new File(inputFileName);
        Lexer lexer = Lexer.getInstance(inputFile);
        TokenStream tokenStream = lexer.lex();
        String output = tokenStream.toString();
        
        try (FileWriter writer = new FileWriter(outputFileName)) {
            writer.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println(output);
        // System.out.println("------------------------------");
        if (ErrorLog.getInstance().getErrorNum() != 0) {
            try (FileWriter writer = new FileWriter(errorFileName)) {
                writer.write(ErrorLog.getInstance().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
