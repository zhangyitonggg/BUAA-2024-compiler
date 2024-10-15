package frontend.parser.AST.SConstInitVal;

import Utils.Printer;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.ConstExp;
import frontend.parser.AnyNode;

import java.util.ArrayList;

// 常量初值 ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
public interface ConstInitVal extends AnyNode {
}
