package frontend.parser.AST.Exp;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.FuncFParams;

// 一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
public class UnaryExp {
    public class SPrimaryExp {
        private PrimaryExp primaryExp;
        
        public SPrimaryExp(PrimaryExp primaryExp) {
            this.primaryExp = primaryExp;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(primaryExp.toString());
            sb.append("<UnaryExp>\n");
            return sb.toString();
        }
    }
    
    public class SFunc {
        private Token ident;
        private FuncRParams funcRParams; // 可能为null
        
        public SFunc(Token ident, FuncRParams funcRParams) {
            this.ident = ident;
            this.funcRParams = funcRParams;
        }
        
        public SFunc(Token ident) {
            this.ident = ident;
            this.funcRParams = null;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(ident.toString());
            sb.append(Printer.ST(TokenType.LPARENT));
            if (funcRParams != null) {
                sb.append(funcRParams.toString());
            }
            sb.append(Printer.ST(TokenType.RPARENT));
            sb.append("<UnaryExp>\n");
            return sb.toString();
        }
    }
    
    public class SUnary {
        private UnaryOp unaryOp;
        private UnaryExp unaryExp;
        
        public SUnary(UnaryOp unaryOp, UnaryExp unaryExp) {
            this.unaryOp = unaryOp;
            this.unaryExp = unaryExp;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(unaryOp.toString());
            sb.append(unaryExp.toString());
            sb.append("<UnaryExp>\n");
            return sb.toString();
        }
    }
}
