package frontend.checker.symbol;

import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.FuncFParam;
import frontend.parser.AST.FuncFParams;
import frontend.parser.AST.FuncType;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<SymType> paramTypes;
    
    public FuncSymbol(Token name, FuncType funcType, FuncFParams funcFParams) {
        super();
        SymType type;
        Token t = funcType.getToken();
        if (t.is(TokenType.INTTK)) {
            type = SymType.IT;
        } else if (t.is(TokenType.CHARTK)) {
            type = SymType.CT;
        } else {
            type = SymType.VT;
        }
        setName(name);
        setType(type);
        paramTypes = new ArrayList<>();
        if (funcFParams != null) {
            for (FuncFParam funcFParam : funcFParams.getFuncFParams()) {
                Token bType = funcFParam.getBType();
                boolean isArray = funcFParam.getIsArray();
                if (bType.is(TokenType.INTTK)) {
                    if (isArray) {
                        paramTypes.add(SymType.AIT);
                    } else {
                        paramTypes.add(SymType.IT);
                    }
                } else {
                    if (isArray) {
                        paramTypes.add(SymType.ACT);
                    } else {
                        paramTypes.add(SymType.CT);
                    }
                }
            }
        }
    }
    
    public int getSize() {
        return paramTypes.size();
    }
    
    public boolean checkType(int index, boolean otherIsArray, boolean otherIsInt) {
        SymType myType = paramTypes.get(index);
        if (otherIsArray) {
            return (myType == SymType.AIT && otherIsInt) || (myType == SymType.ACT && !otherIsInt);
        } else {
            return (myType == SymType.IT) || (myType == SymType.CT);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name).append(" ");
        if (this.type == SymType.VT) {
            sb.append("VoidFunc");
        } else if (this.type == SymType.CT) {
            sb.append("CharFunc");
        } else {
            sb.append("IntFunc");
        }
        sb.append("\n");
        return sb.toString();
    }
}
