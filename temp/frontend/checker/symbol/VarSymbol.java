package frontend.checker.symbol;

import frontend.lexer.Token;
import frontend.lexer.TokenType;
import frontend.parser.AnyNode;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    private boolean isConst;
    private boolean isGlobal;
    private int elmNums;
    private ArrayList<Integer> inits;
    
    public VarSymbol(Token name, Token bType, AnyNode anyNode, boolean isConst, boolean isGlobal) {
        super();
        SymType type;
        if (bType.is(TokenType.INTTK)) {
            if (anyNode != null) {
                type = SymType.AIT;
            } else {
                type = SymType.IT;
            }
        } else {
            if (anyNode != null) {
                type = SymType.ACT;
            } else {
                type = SymType.CT;
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
        SymType type;
        if (bType.is(TokenType.INTTK)) {
            if (isArray) {
                type = SymType.AIT;
            } else {
                type = SymType.IT;
            }
        } else {
            if (isArray) {
                type = SymType.ACT;
            } else {
                type = SymType.CT;
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
        return this.type == SymType.AIT || this.type == SymType.ACT;
    }
    
    public boolean isInt() {
        return this.type == SymType.IT || this.type == SymType.AIT;
    }

    public void setInits(ArrayList<Integer> inits) {
        this.inits = inits;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name).append(" ");
        if (isConst) {
            sb.append("Const");
        }
        if (this.type == SymType.IT || this.type == SymType.AIT) {
            sb.append("Int");
        } else {
            sb.append("Char");
        }
        if (this.type == SymType.AIT || this.type == SymType.ACT) {
            sb.append("Array");
        }
        sb.append("\n");
        return sb.toString();
    }
}
