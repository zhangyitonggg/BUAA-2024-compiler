package frontend;
import Utils.Error;
import Utils.ErrorLog;

import java.io.*;

public class Lexer {
    private static Lexer lexer;
    private final PushbackReader reader;
    private int line = 1;
    
    private char temp;
    
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
    
    public TokenStream lex() throws IOException {
        TokenStream tokenStream = new TokenStream();
        while(true) {
            skipSpace();
            int ch = reader.read();
            if (ch == -1) {
                break;
            } else if (ch == '\n' || ch == '\r') {
                if (ch == '\n') {
                    line++;
                }
                continue;
            } else {
                reader.unread(ch);
            }
            Token token = findToken();
            if (token != null) {
                tokenStream.addToken(token);
            }
        }
        return tokenStream;
    }
    
    public Token findToken() throws IOException {
        int ch = reader.read();
        if (isSpecial(ch)) {               // 特殊的字符
            return tackleSpecial(ch);
        } else if (ch == '\"') {               // 字符串常量
            return tackleStringConst(ch);
        } else if (Character.isDigit(ch)) {
            return tackleIntConst(ch);
        } else if (ch == '\'') {
            return tackleCharConst(ch);
        } else {
            return tackleIdent(ch);
        }
    }
    
    public boolean isSpecial(int ch) {
        return ch == '!' || ch == '&' || ch == '|' ||
               ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' ||
               ch == '<' || ch == '>' || ch == '=' ||
               ch == ';' || ch == ',' ||
               ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == '{' || ch == '}';
    }
    
    public Token tackleSpecial(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (ch == '!') {
            temp = (char) ch;
            sb.append(temp);
            ch = reader.read();
            if (ch == '=') {
                temp = (char) ch;
                sb.append(temp);
                return new Token(TokenType.NEQ, sb.toString(), line);
            } else {
                reader.unread(ch);
                return new Token(TokenType.NOT, sb.toString(), line);
            }
        } else if (ch == '&') {
            temp = (char) ch;
            sb.append(temp);
            ch = reader.read();
            if (ch == '&') {
                temp = (char) ch;
                sb.append(temp);
            } else {
                ErrorLog.getInstance().addError(new Error(line, 'a'));
                reader.unread(ch);
            }
            return new Token(TokenType.AND, sb.toString(), line);
        } else if (ch == '|') {
            temp = (char) ch;
            sb.append(temp);
            ch = reader.read();
            if (ch == '|') {
                temp = (char) ch;
                sb.append(temp);
            } else {
                ErrorLog.getInstance().addError(new Error(line, 'a'));
                reader.unread(ch);
            }
            return new Token(TokenType.OR, sb.toString(), line);
        } else if (ch == '+') {
            return new Token(TokenType.PLUS, ch, line);
        } else if (ch == '-') {
            return new Token(TokenType.MINU, ch, line);
        } else if (ch == '*') {
            return new Token(TokenType.MULT, ch, line);
        } else if (ch == '/') {
            Token  token = new Token(TokenType.DIV, ch, line);
            ch = reader.read();
            if (ch == '/' || ch == '*') {
                tackleComment(ch);
                return null;
            } else {
                reader.unread(ch);
                return token;
            }
        } else if (ch == '%') {
            return new Token(TokenType.MOD, ch, line);
        } else if (ch == '<') {
            temp = (char) ch;
            sb.append(temp);
            ch = reader.read();
            if (ch == '=') {
                temp = (char) ch;
                sb.append(temp);
                return new Token(TokenType.LEQ, sb.toString(), line);
            } else {
                reader.unread(ch);
                return new Token(TokenType.LSS, sb.toString(), line);
            }
        } else if (ch == '>') {
            temp = (char) ch;
            sb.append(temp);
            ch = reader.read();
            if (ch == '=') {
                temp = (char) ch;
                sb.append(temp);
                return new Token(TokenType.GEQ, sb.toString(), line);
            } else {
                reader.unread(ch);
                return new Token(TokenType.GRE, sb.toString(), line);
            }
        } else if (ch == '=') {
            temp = (char) ch;
            sb.append(temp);
            ch = reader.read();
            if (ch == '=') {
                temp = (char) ch;
                sb.append(temp);
                return new Token(TokenType.EQL, sb.toString(), line);
            } else {
                reader.unread(ch);
                return new Token(TokenType.ASSIGN, sb.toString(), line);
            }
        } else if (ch == ';') {
            return new Token(TokenType.SEMICN, ch, line);
        } else if (ch == ',') {
            return new Token(TokenType.COMMA, ch, line);
        } else if (ch == '(') {
            return new Token(TokenType.LPARENT, ch, line);
        } else if (ch == ')') {
            return new Token(TokenType.RPARENT, ch, line);
        } else if (ch == '[') {
            return new Token(TokenType.LBRACK, ch, line);
        } else if (ch == ']') {
            return new Token(TokenType.RBRACK, ch, line);
        } else if (ch == '{') {
            return new Token(TokenType.LBRACE, ch, line);
        } else if (ch == '}') {
            return new Token(TokenType.RBRACE, ch, line);
        } else {
            System.out.println("bug\n");
            return null;
        }
    }
    
    public Token tackleStringConst(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        temp = (char) ch;
        sb.append(temp);
        while(true) {
            ch = reader.read();
            temp = (char) ch;
            sb.append(temp);
            if (ch == '\"') {
                break;
            }
        }
        return new Token(TokenType.STRCON, sb.toString(), line);
    }
    
    public Token tackleIntConst(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        temp = (char) ch;
        sb.append(temp);
        while(true) {
            ch = reader.read();
            if (Character.isDigit(ch)) {
                temp = (char) ch;
                sb.append(temp);
            } else {
                reader.unread(ch);
                break;
            }
        }
        return new Token(TokenType.INTCON, sb.toString(), line);
    }
    
    public Token tackleCharConst(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        temp = (char) ch;
        sb.append(temp);
        ch = reader.read();
        temp = (char) ch;
        sb.append(temp);
        if (ch == '\\') {
            ch = reader.read();
            temp = (char) ch;
            sb.append(temp);
        }
        ch = reader.read();
        temp = (char) ch;
        sb.append(temp);
        return new Token(TokenType.CHRCON, sb.toString(), line);
    }
    
    public Token tackleIdent(int ch) throws IOException {
        StringBuilder sb = new StringBuilder();
        temp = (char) ch;
        sb.append(temp);
        while(true) {
            ch = reader.read();
            if (!(ch == '_' || Character.isLetterOrDigit(ch))) {
                reader.unread(ch);
                break;
            }
            temp = (char) ch;
            sb.append(temp);
        }
        String s = sb.toString();
        TokenType type;
        if (s.equals("main")) {
            type = TokenType.MAINTK;
        } else if (s.equals("const")) {
            type = TokenType.CONSTTK;
        } else if (s.equals("int")) {
            type = TokenType.INTTK;
        } else if (s.equals("char")) {
            type = TokenType.CHARTK;
        } else if (s.equals("break")) {
            type = TokenType.BREAKTK;
        } else if (s.equals("continue")) {
            type = TokenType.CONTINUETK;
        } else if (s.equals("if")) {
            type = TokenType.IFTK;
        } else if (s.equals("else")) {
            type = TokenType.ELSETK;
        } else if (s.equals("for")) {
            type = TokenType.FORTK;
        } else if (s.equals("getint")) {
            type = TokenType.GETINTTK;
        } else if (s.equals("getchar")) {
            type = TokenType.GETCHARTK;
        } else if (s.equals("printf")) {
            type = TokenType.PRINTFTK;
        } else if (s.equals("return")) {
            type = TokenType.RETURNTK;
        } else if (s.equals("void")) {
            type = TokenType.VOIDTK;
        } else {
            type = TokenType.IDENFR;
        }
        return new Token(type, s, line);
    }
    
    public void tackleComment(int ch) throws IOException {
        if (ch == '/') { // 单行注释
            while(true) {
                ch = reader.read();
                if (ch == -1) {
                    return;
                } else if (ch == '\n') {
                    reader.unread(ch);
                    return;
                }
            }
        } else { // 多行注释
            while(true) {
                ch = reader.read();
                if (ch == -1) {
                    return;
                } else if (ch == '*') {
                    int nextCh = reader.read();
                    if (nextCh == -1) {
                        return;
                    } else if (nextCh == '/') {
                        return;
                    } else {
                        reader.unread(nextCh);
                    }
                } else if (ch == '\n') {
                    line++;
                }
            }
        }
    }
    
    public void skipSpace() throws IOException {
        int ch;
        while (true) {
            ch = reader.read();
            if (ch == -1) {
                return;
            }
            else if (!(ch == ' ' || ch == '\t' || ch == '\r')) {
                reader.unread(ch);
                return;
            }
        }
    }
}
