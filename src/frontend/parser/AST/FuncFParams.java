package frontend.parser.AST;

import Utils.Printer;
import frontend.lexer.TokenType;
import frontend.parser.AnyNode;

import java.util.ArrayList;

// 函数形参表 FuncFParams → FuncFParam { ',' FuncFParam }
public class FuncFParams implements AnyNode {
    private ArrayList<FuncFParam> funcFParams;
    
    public FuncFParams(ArrayList<FuncFParam> funcFParams) {
        this.funcFParams = funcFParams;
    }
    
    public ArrayList<FuncFParam> getFuncFParams() {
        return funcFParams;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        for(FuncFParam funcFParam : funcFParams) {
            if (flag) {
                sb.append(Printer.ST(TokenType.COMMA));
                sb.append(funcFParam.toString());
            } else {
                sb.append(funcFParam.toString());
                flag = true;
            }
        }
        sb.append("<FuncFParams>\n");
        return sb.toString();
    }
}
