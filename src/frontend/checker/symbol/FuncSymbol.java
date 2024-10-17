package frontend.checker.symbol;

import frontend.checker.SymbolTable;
import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AST.Exp.FuncRParams;
import frontend.parser.AST.FuncFParam;
import frontend.parser.AST.FuncFParams;
import frontend.parser.AST.FuncType;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<Type> paramTypes;
    
//    public FuncSymbol(Token name, Type returnType, ArrayList<Type> paramTypes) {
//        super(name, returnType);
//        this.paramTypes = paramTypes;
//    }
    
    public FuncSymbol(Token name, FuncType funcType, FuncFParams funcFParams) {
        super();
        Type type;
        Token t = funcType.getToken();
        if (t.is(TokenType.INTTK)) {
            type = Type.IT;
        } else if (t.is(TokenType.CHARTK)) {
            type = Type.CT;
        } else {
            type = Type.VT;
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
                        paramTypes.add(Type.AIT);
                    } else {
                        paramTypes.add(Type.IT);
                    }
                } else {
                    if (isArray) {
                        paramTypes.add(Type.ACT);
                    } else {
                        paramTypes.add(Type.CT);
                    }
                }
            }
        }
    }
    
    public int getSize() {
        return paramTypes.size();
    }
    
    public boolean checkType(int index, boolean otherIsArray, boolean otherIsInt) {
        Type myType = paramTypes.get(index);
        if (otherIsArray) {
            return (myType == Type.AIT && otherIsInt) || (myType == Type.ACT && !otherIsInt);
        } else {
            return (myType == Type.IT) || (myType == Type.CT);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name).append(" ");
        if (this.type == Type.VT) {
            sb.append("VoidFunc");
        } else if (this.type == Type.CT) {
            sb.append("CharFunc");
        } else {
            sb.append("IntFunc");
        }
        sb.append("\n");
        return sb.toString();
    }
}
