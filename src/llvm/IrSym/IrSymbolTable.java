package llvm.IrSym;

import frontend.checker.SymbolTable;
import frontend.checker.symbol.Symbol;
import llvm.value.Value;
import java.util.HashMap;

public class IrSymbolTable {
    private IrSymbolTable father;
    private final HashMap<String, Value> symbols;
    
    public IrSymbolTable(IrSymbolTable father) {
        this.father = father;
        this.symbols = new HashMap<>();
    }
    
    public IrSymbolTable getFather() {
        return this.father;
    }
    
    public void addSymbol(String name, Value value) {
        symbols.put(name, value);
    }
    
    public Value find(String name) {
        IrSymbolTable symbolTable = this;
        while(symbolTable != null) {
            if (symbolTable.symbols.containsKey(name)) {
                return symbolTable.symbols.get(name);
            }
            symbolTable = symbolTable.getFather();
        }
        return null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : symbols.keySet()) {
            sb.append(s).append(" ");
        }
        sb.append("\n");
        if (father != null) {
            sb.append(father.toString());
        }
        return sb.toString();
    }
}
