package llvm.types;

import java.util.ArrayList;

public class FuncIrTy extends IrTy {
    public final DataIrTy returnTy;
    
    public final ArrayList<DataIrTy> fParamTys;
    
    public FuncIrTy(DataIrTy returnTy, ArrayList<DataIrTy> fParamTys) {
        this.returnTy = returnTy;
        this.fParamTys = fParamTys;
    }
    
    @Override
    public int getByte() {
        return returnTy.getByte();
    }
    
    @Override
    public int getAlign() {
        return returnTy.getByte();
    }
    
    @Override
    public boolean isFunction() {
        return true;
    }
}
