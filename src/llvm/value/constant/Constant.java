package llvm.value.constant;

import llvm.types.IrTy;
import llvm.value.User;
import llvm.value.Value;

import java.util.ArrayList;

public class Constant extends Value {
    protected Constant(IrTy type) {
        super(null, type);
    }
    
    /**
     * @return constData是否是char或者constArray是否由char组成
     */
    public boolean hasChar() {
        return false;
    }
    
    public ArrayList<Integer> getAllNum() {
        return null;
    }
}
