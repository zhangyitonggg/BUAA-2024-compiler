package frontend.parser.AST.Exp.SUnaryExp;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.FuncRParams;
import frontend.parser.AST.Exp.SPrimaryExp.PrimaryExp;

// 一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
public interface UnaryExp {
}
