import frontend.Lexer;

import java.io.File;
import java.io.FileReader;

public class Compiler {
    private final static String inputFileName = "testfile.txt";
    private final static String outputFileName = "output.txt";
    
    public static void main(String[] args) {
        File inputFile = new File(inputFileName);
        Lexer lexer = Lexer.getInstance(inputFile);
        
    }
}
