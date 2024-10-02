package Utils;

import frontend.lexer.TokenType;

import java.io.FileWriter;
import java.io.IOException;

public class Printer {
    private final static String lexerFileName = "lexer.txt";
    private final static String errorFileName = "error.txt";
    private final static String parserFileName = "parser.txt";
    
    private static void print2file(String fileName, String output) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void print2lexer(String output) {
        print2file(lexerFileName, output);
    }
    
    public static void print2parser(String output) {
        print2file(parserFileName, output);
    }
    public static void print2error(String output) {
        print2file(errorFileName, output);
    }

    public static String ST(TokenType tokenType) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append(tokenType.toString());
        switch (tokenType){
            case MAINTK -> sb.append("main");
            case CONSTTK -> sb.append("const");
            case INTTK -> sb.append("int");
            case CHARTK -> sb.append("char");
            case BREAKTK -> sb.append("break");
            case CONTINUETK -> sb.append("continue");
            case IFTK -> sb.append("if");
            case ELSETK -> sb.append("else");
            case NOT -> sb.append("!");
            case AND -> sb.append("&&");
            case OR -> sb.append("||");
            case FORTK -> sb.append("for");
            case GETINTTK -> sb.append("getint");
            case GETCHARTK -> sb.append("getchar");
            case PRINTFTK -> sb.append("printf");
            case RETURNTK -> sb.append("return");
            case PLUS -> sb.append("+");
            case MINU -> sb.append("-");
            case VOIDTK -> sb.append("void");
            case MULT -> sb.append("*");
            case DIV -> sb.append("%");
            case MOD -> sb.append("%");
            case LSS -> sb.append("<");
            case LEQ -> sb.append("<=");
            case GRE -> sb.append(">");
            case GEQ -> sb.append(">=");
            case EQL -> sb.append("==");
            case NEQ -> sb.append("!=");
            case ASSIGN -> sb.append("=");
            case SEMICN -> sb.append(";");
            case COMMA -> sb.append(",");
            case LPARENT -> sb.append("(");
            case RPARENT -> sb.append(")");
            case LBRACK -> sb.append("[");
            case RBRACK -> sb.append("]");
            case LBRACE -> sb.append("{");
            case RBRACE -> sb.append("}");
            default -> {
                System.out.println("无法匹配！\n");
                sb.append(" ");
            }
        }
        sb.append("\n");
        return sb.toString();
    }
}
