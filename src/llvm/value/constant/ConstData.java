package llvm.value.constant;

import llvm.types.DataIrTy;

// 常量是无所谓类型的
public class ConstData extends Constant {
    private final int value;

    // 这里将常数全部理解成i32类型的，实际上目前我认为ConstData的类型是不重要的。
    public ConstData(DataIrTy type, int value) {
        super(type);
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public String getName() {
        return toString();
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
