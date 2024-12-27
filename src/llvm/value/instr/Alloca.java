package llvm.value.instr;

import llvm.types.DataIrTy;
import llvm.types.IrTy;
import llvm.types.PointerIrTy;
import llvm.value.Value;
import llvm.value.constant.Constant;

public class Alloca extends Instruction{
    private final Constant init; // 只是初始值，并不意味着是常量。为null时意味着没有初始化
    
    // 非常量
    public Alloca(Value host, int nameCount, IrTy irTy) {
        super(host, "%v" + nameCount, new PointerIrTy(irTy));
        this.init = null;
    }
    
    // 常量
    public Alloca(Value host, int nameCount, IrTy irTy, Constant init) {
        super(host, "%v" + nameCount, new PointerIrTy(irTy));
        this.init = init;
    }
    
    public Constant getInit() {
        return init;
    }
    
    @Override
    public String toString() {
        return getName() + " = alloca " + ((PointerIrTy) getType()).deRefIrTy;
    }
}
