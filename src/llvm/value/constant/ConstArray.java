package llvm.value.constant;

import llvm.types.ArrayIrTy;
import llvm.types.DataIrTy;
import llvm.types.IrTy;

import java.util.ArrayList;

/**
 * 感觉这个设计很畸形,这里有type只是为了：
 * 1. 输出时好输出
 * 2. 填补空缺
 */

public class ConstArray extends Constant {
    private final ArrayList<Integer> elements;
    
    public ConstArray(ArrayIrTy type, ArrayList<Integer> elements) {
        super(type);
        this.elements = elements;
        // ConstArray仅仅用于全局数组的初始化，elements长度可能小于数组长度，需要补0
        int length = type.length;
        for (int i = 0; i < length - elements.size(); i++) {
            this.elements.add(0);
        }
    }
    
    /**
     * 全部置0
     * @param type
     */
    public ConstArray(ArrayIrTy type) {
        super(type);
        this.elements = new ArrayList<>();
        int length = type.length;
        for (int i = 0; i < length - elements.size(); i++) {
            this.elements.add(0);
        }
    }
    
    public boolean isZero() {
        for (int element : elements) {
            if (element != 0) {
                return false;
            }
        }
        return true;
    }

    public int getLength() {
        return elements.size();
    }
    
    public ConstData getElement(int i) {
        if (i < elements.size()) {
            return new ConstData(((ArrayIrTy) getType()).eleIrTy, elements.get(i));
        }
        return null;
    }
    
    @Override
    public String toString() {
        ArrayIrTy type = (ArrayIrTy) getType();
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        DataIrTy elementType = type.eleIrTy;
        for (int i = 0; i < type.length; i++) {
            sb.append(elementType.toString());
            sb.append(" ");
            sb.append(elements.get(i).toString());
            if (i == type.length - 1) {
                break;
            }
            sb.append(", ");
        }
        sb.append(']');
        return sb.toString();
    }
}
