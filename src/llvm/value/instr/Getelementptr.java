package llvm.value.instr;

import llvm.types.ArrayIrTy;
import llvm.types.DataIrTy;
import llvm.types.IrTy;
import llvm.types.PointerIrTy;
import llvm.value.Value;

/**
 * <result> = getelementptr <ty>, ptr <ptrval>{, <ty> <idx>}*
 * 仅考虑一维数组，
 * 对于正常的数组寻址，有下面的格式：
 * %8 = getelementptr inbounds [6 x i32], [6 x i32]* %7, i32 0, i32 2
 * 对于这种两参数的类型，返回值类型为i32*,也就是数组的元素的指针类型
 *  call void @putstr(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str, i64 0, i64 0))
 *  返回值类型为i8*
 *
 *  而对于函数传参，实际上是传的指针，有下面的格式：
 *  %7 = getelementptr inbounds i32, i32* %6, i32 2
 *  此时返回值类型为i32*，也就直接是传入的指针类型。
 *
 *  getelementptr baseType, baseType* base, A, B;   ----    baseType.getElementType*
 *  getelementptr baseType, baseType* base, A;      ----    baseType*
 */
public class Getelementptr extends Instruction {
    private final int indexNum;
    private final IrTy baseType;
    
    /**
     * 用于正常寻址
     * getelementptr baseType, baseType* base, A, B;   ----    baseType.getElementType*
     * 对于我们的文法，A和B只可能是0
     * @param nameCount
     * @param base
     * @param leftIndex
     * @param rightIndex
     */
    public Getelementptr(int nameCount, Value base, Value leftIndex, Value rightIndex) {
        super("%v" + nameCount, new PointerIrTy(((ArrayIrTy) calcBaseType(base)).eleIrTy));
        this.indexNum = 2;
        this.baseType = calcBaseType(base);
        addOperand(base);
        addOperand(leftIndex);
        addOperand(rightIndex);
    }
    
    /**
     * 用于函数传参
     * getelementptr baseType, baseType* base, A;      ----    baseType*
     * baseType* 实际上就是base的type
     * @param nameCount
     * @param base
     * @param leftIndex
     */
    public Getelementptr(int nameCount, Value base, Value leftIndex) {
        super("%v" + nameCount, (PointerIrTy) base.getType());
        this.indexNum = 1;
        this.baseType = calcBaseType(base);
        addOperand(base);
        addOperand(leftIndex);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(" = getelementptr inbounds ");
        sb.append(baseType);
        sb.append(", ");
        // base
        Value base = getOperand(0);
        sb.append(base.getType());
        sb.append(" ");
        sb.append(base.getName());
        sb.append(", ");
        // leftIndex
        Value leftIndex = getOperand(1);
        sb.append(leftIndex.getType());
        sb.append(" ");
        sb.append(leftIndex.getName());
        sb.append(", ");
        // rightIndex
        if (indexNum == 2) {
            Value rightIndex = getOperand(2);
            sb.append(rightIndex.getType());
            sb.append(" ");
            sb.append(rightIndex.getName());
        }
        return sb.toString();
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static IrTy calcBaseType(Value base) {
        return ((PointerIrTy) (base.getType())).deRefIrTy;
    }
    
}
