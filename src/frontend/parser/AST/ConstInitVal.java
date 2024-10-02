package frontend.parser.AST;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.ConstExp;

import java.util.ArrayList;

// 常量初值 ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
public class ConstInitVal {
    public class SConstExp extends ConstInitVal {
        private ArrayList<ConstExp> constExps;
        
        public SConstExp(ArrayList<ConstExp> constExps) {
            this.constExps = constExps;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(constExps.get(0).toString());
            sb.append("<ConstInitVal>\n");
            return sb.toString();
        }
    }
    
    public class SConstExps extends ConstInitVal {
        private ArrayList<ConstExp> constExps;
        
        public SConstExps(ArrayList<ConstExp> constExps) {
            this.constExps = constExps;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(Printer.ST(TokenType.LBRACE));
            boolean flag = false;
            for(ConstExp constExp : constExps) {
                if (flag) {
                    sb.append(Printer.ST(TokenType.COMMA));
                    sb.append(constExp.toString());
                } else {
                    sb.append(constExp.toString());
                    flag = true;
                }
            }
            sb.append(Printer.ST(TokenType.RBRACE));
            sb.append("<ConstInitVal>\n");
            return sb.toString();
        }
    }
    
    public class SStringConst extends ConstInitVal {
        Token stringConst;
        
        public SStringConst(Token stringConst) {
            this.stringConst = stringConst;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(stringConst.toString());
            sb.append("<ConstInitVal>\n");
            return sb.toString();
        }
    }
}
