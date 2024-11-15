package llvm.value.constant;

import llvm.types.IrTy;
import llvm.value.User;
import llvm.value.Value;

public class Constant extends Value {
    public Constant(IrTy type) {
        super(type);
    }
    
}
