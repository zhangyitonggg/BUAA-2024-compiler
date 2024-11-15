package llvm.value.notInstr;

import llvm.types.DataIrTy;
import llvm.types.IrTy;
import llvm.value.Value;

public class FParam extends Value {
    // index是参数编号，0、1、2
    public FParam(int index, DataIrTy dataIrTy) {
        super("%a" + index, dataIrTy);
    }
    
    public int getIndex() {
        // 使用正则表达式匹配数字部分
        String number = getName().replaceAll("[^0-9]", "");
        // 将提取的数字字符串转换为整数
        int index = Integer.parseInt(number);
        return index;
    }
}
