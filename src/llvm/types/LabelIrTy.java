package llvm.types;

public class LabelIrTy extends IrTy {
    @Override
    public int getByte() {
        System.out.println("你调这个干啥：" + getClass());
        return 0;
    }
    
    @Override
    public int getAlign() {
        System.out.println("你调这个干啥：" + getClass());
        return 0;
    }
    
    @Override
    public String toString() {
        return "label";
    }
}
