package llvm.types;

public class ArrayIrTy extends IrTy {
    // 只可能是i32或者是i8
    public final DataIrTy eleIrTy;
    // 数组长度
    public final int length;
    
    public ArrayIrTy(IrTy eleIrTy, int length) {
        this.eleIrTy = (DataIrTy) eleIrTy;
        this.length = length;
    }
    
    @Override
    public boolean isArray() {
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(length);
        sb.append(" x ");
        sb.append(eleIrTy.toString());
        sb.append(']');
        return sb.toString();
    }
}
