package llvm.types;

public class IrTy {

    public boolean isBCI() {
        return false;
    }
    
    public boolean isPointer() {
        return false;
    }
    
    public boolean isArray() {
        return false;
    }
    
    public boolean isFunction() {
        return false;
    }
    
    public int getByte() {
        return 0;
    }
    
    public int getAlign() {
        return 0;
    }
}
