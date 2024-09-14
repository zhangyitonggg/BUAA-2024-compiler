package frontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PushbackReader;

public class Lexer {
    private static Lexer lexer;
    private final PushbackReader reader;
    private int line = 0;
    
    private Lexer(File file)  {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            System.err.println("没有输入程序--" + e);
            System.exit(-1);
        }
        reader = new PushbackReader(fileReader);
    }
    
    public static Lexer getInstance(File file) {
        if (null == lexer) {
            lexer = new Lexer(file);
        }
        return lexer;
    }
    
    public TokenStream lex() {
        TokenStream tokenStream = new TokenStream();
        while(true) {
            Token token = findToken();
            if (null == token) {
                break;
            }
            tokenStream.addToken(token);
        }
        return tokenStream;
    }
    
    public Token findToken() {
        while()
        
        return null;
    }
    
    public void skipspace() {
    
    }
}
