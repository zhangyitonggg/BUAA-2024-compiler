package llvm.value.notInstr;

import llvm.types.ArrayIrTy;
import llvm.types.IrTy;
import llvm.types.PointerIrTy;
import llvm.value.User;
import llvm.value.Value;
import llvm.value.constant.ConstArray;
import llvm.value.constant.Constant;

/**
 * 要么是全局的数组，要么是全局的普通变量
 * @a = dso_local global i32 97
 * @a = dso_local global [10 x i32] [i32 1, i32 2, i32 3, i32 0, i32 0, i32 0, i32 0, i32 0, i32 0, i32 0]
 * @b = dso_local global [20 x i32] zeroinitializer
 * @c = dso_local global [8 x i8] c"foobar\00\00", align 1
 */
public class GlobalVar extends User {
    private final Constant init;
    private final boolean isConst;
    
    /**
     * 常量数组不需要每一个元素都有初始值，但是未赋值的部分编译器需要将其置0
     * @param name
     * @param init 这里我们要求init如果是数组，那么其长度必须和数组长度一样，与isConst无关
     * @param isConst
     */
    public GlobalVar(String name, Constant init, boolean isConst) {
        super("@" + name, new PointerIrTy(init.getType()));
        this.init = init;
        this.isConst = isConst;
    }
    
    public Constant getInit(){
        return init;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(" = dso_local ");
        if (isConst) {
            sb.append("constant ");
        } else {
            sb.append("global ");
        }
        IrTy type = ((PointerIrTy) getType()).deRefIrTy;
        sb.append(type.toString());
        sb.append(" ");
        if (type instanceof ArrayIrTy) {
            if (init == null || ((ConstArray) init).isZero()) {
                sb.append("zeroinitializer");
            } else {
                sb.append(init.toString());
            }
        } else {
            sb.append(init.toString());
        }
        return sb.toString();
    }
}

