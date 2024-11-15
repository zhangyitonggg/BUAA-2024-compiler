package llvm.value;

import llvm.Use;
import llvm.types.IrTy;

import java.util.ArrayList;

public class Value {
    private static int idCount = 0;
    private final int id; // 给每一个独立的Value提供一个标示，用于区分
    private String name;
    private final IrTy type;
    private final ArrayList<Use> uses; // 里面存的Use关系的Used是自己
    
    public Value(String name, IrTy type) {
        this.id = idCount;
        this.name = name;
        this.type = type;
        this.uses = new ArrayList<>();
        
        idCount += 1;
    }
    
    public Value(IrTy type) {
        this.id = idCount;
        this.name = null;
        this.type = type;
        this.uses = new ArrayList<>();
        
        idCount += 1;
    }
    
    public void addUse(User user) {
        Use use = new Use(user, this);
        uses.add(use);
    }
    
    public IrTy getType() {
        return type;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}

