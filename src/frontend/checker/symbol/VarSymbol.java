package frontend.checker.symbol;

import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AnyNode;

public class VarSymbol extends Symbol {
    private boolean isConst;
    private boolean isGlobal;
    
    public VarSymbol(Token name, Token bType, AnyNode anyNode, boolean isConst, boolean isGlobal) {
        super();
        Type type;
        if (bType.is(TokenType.INTTK)) {
            if (anyNode != null) {
                type = Type.AIT;
            } else {
                type = Type.IT;
            }
        } else {
            if (anyNode != null) {
                type = Type.ACT;
            } else {
                type = Type.CT;
            }
        }
        this.setName(name);
        this.setType(type);
        this.isConst = isConst;
        this.isGlobal = isGlobal;
    }
    
    // FuncFParam
    public VarSymbol(Token name, Token bType, Boolean isArray) {
        super();
        Type type;
        if (bType.is(TokenType.INTTK)) {
            if (isArray) {
                type = Type.AIT;
            } else {
                type = Type.IT;
            }
        } else {
            if (isArray) {
                type = Type.ACT;
            } else {
                type = Type.CT;
            }
        }
        this.setName(name);
        this.setType(type);
        this.isConst = false;
        this.isGlobal = false;
    }
    
    public boolean isConst() {
        return isConst;
    }
    
    public boolean isGlobal() {
        return isGlobal;
    }
    
    public boolean isArray() {
        return this.type == Type.AIT || this.type == Type.ACT;
    }
    
    public boolean isInt() {
        return this.type == Type.IT || this.type == Type.AIT;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name).append(" ");
        if (isConst) {
            sb.append("Const");
        }
        if (this.type == Type.IT || this.type == Type.AIT) {
            sb.append("Int");
        } else {
            sb.append("Char");
        }
        if (this.type == Type.AIT || this.type == Type.ACT) {
            sb.append("Array");
        }
        sb.append("\n");
        return sb.toString();
    }
}
