package llvm.value.constant;

import llvm.types.DataIrTy;

import java.util.ArrayList;

// 常量是无所谓类型的
public class ConstData extends Constant {
    private final int value;

    private final boolean justPlaceholder;
    public ConstData(DataIrTy type, int value) {
        super(type);
        this.value = value;
        this.justPlaceholder = false;
    }
    
    public ConstData(DataIrTy type, int value, boolean justPlaceholder) {
        super(type);
        this.value = value;
        this.justPlaceholder = justPlaceholder;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public boolean hasChar() {
        return ((DataIrTy) getType()).isI8();
    }
    
    @Override
    public ArrayList<Integer> getAllNum() {
        ArrayList<Integer> res = new ArrayList<>();
        res.add(value);
        return res;
    }
    
    @Override
    public String getName() {
        return toString();
    }
    
    @Override
    public boolean isJustPlaceholder() {
        return justPlaceholder;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
