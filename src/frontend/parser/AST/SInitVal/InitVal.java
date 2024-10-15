package frontend.parser.AST.SInitVal;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.Exp;
import frontend.parser.AnyNode;

import java.util.ArrayList;

// 变量初值 InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
public interface InitVal extends AnyNode {
}