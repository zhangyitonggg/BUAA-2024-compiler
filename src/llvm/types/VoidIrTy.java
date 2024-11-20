package llvm.types;

public class VoidIrTy extends DataIrTy {
    @Override
    public int getByte() {
        System.out.println("你调这个干啥：" + getClass());
        return 0;
    }
    
    @Override
    public int getAlign() {
        System.out.println("你调这个干嘛：" + getClass());
        return 0;
    }
    
    @Override
    public String toString() {
        return "void";
    }
}
