package llvm.types;

public class PointerIrTy extends DataIrTy {
    /**
     * 只可能是 i8 or i32
     */
    public final IrTy deRefIrTy;
    
    public PointerIrTy(IrTy deRefIrTy) {
        super();
        this.deRefIrTy = deRefIrTy;
    }
    
    @Override
    public boolean isPointer() {
        return true;
    }
    
    @Override
    public String toString() {
        return deRefIrTy.toString() + "*";
    }
}
