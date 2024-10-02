package frontend.parser.AST.Exp;

import Utils.Printer;
import frontend.lexer.TokenType;

// 基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number | Character
public class PrimaryExp {
    public class SExp extends PrimaryExp {
        private Exp exp;
        
        public SExp(Exp exp) {
            this.exp = exp;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(Printer.ST(TokenType.LPARENT));
            sb.append(exp.toString());
            sb.append(Printer.ST(TokenType.RPARENT));
            sb.append("<PrimaryExp>\n");
            return sb.toString();
        }
    }
    
    public class SLVal extends PrimaryExp {
        private LVal lVal;
        
        public SLVal(LVal lVal) {
            this.lVal = lVal;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(lVal.toString());
            sb.append("<PrimaryExp>\n");
            return sb.toString();
        }
    }
    
    public class SNumber extends PrimaryExp {
        private Number number;
        
        public SNumber(Number number) {
            this.number = number;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(number.toString());
            sb.append("<PrimaryExp>\n");
            return sb.toString();
        }
    }
    
    public class SCharacter extends PrimaryExp {
        private Character character;
        
        public SCharacter(Character character) {
            this.character = character;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(character.toString());
            sb.append("<PrimaryExp>\n");
            return sb.toString();
        }
    }
}
