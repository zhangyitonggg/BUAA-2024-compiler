package llvm.types;

public class DataIrTy extends IrTy {
    public static final DataIrTy I1 = new DataIrTy(1);
    public static final DataIrTy I8 = new DataIrTy(8);
    public static final DataIrTy I32 = new DataIrTy(32);
    
    private final int bits;
    private DataIrTy(int bits) {
        this.bits = bits;
    }
    
    /**
     * 仅限指针类型调用
     */
    public DataIrTy() {
        this.bits = 32;
    }
    
    @Override
    public boolean isBCI() {
        return isI1() || isI8() || isI32();
    }
    
    // 什么逆天写法，竟然还比起地址来了
    public boolean isI1() {
        return this == I1;
    }
    
    public boolean isI8() {
        return this == I8;
    }
    
    public boolean isI32() {
        return this == I32;
    }
    
    public boolean isVoid() {
        return this instanceof VoidIrTy;
    }
    
    public boolean less(DataIrTy other) {
        if (this.isI1()) {
            return other.isI8() || other.isI32();
        } else if (this.isI8()) {
            return other.isI32();
        }
        return false;
    }
    
    public boolean more(DataIrTy other) {
        if (this.isI8()) {
            return other.isI1();
        } else if (this.isI32()) {
            return other.isI8() || other.isI1();
        }
        return false;
    }
    
    public boolean equals(DataIrTy other) {
        return !less(other) && !more(other);
    }
    
    
    @Override
    public String toString() {
        return "i" + bits;
    }
}
