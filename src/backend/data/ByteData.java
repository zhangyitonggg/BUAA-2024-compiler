package backend.data;

import java.util.ArrayList;

/**
 *.需要初始化，对应char
 * nums: .byte 1, 2, 3, 4  # 分配4个字节，并初始化为1, 2, 3, 4
 * str:  .byte 'H', 'i', 0  # 分配3个字节，存储字符串"Hi\0"
 */
public class ByteData extends Data {
    private ArrayList<Integer> inits;
    
    public ByteData(String name, ArrayList<Integer> inits) {
        super(name);
        if (inits == null) {
            inits = new ArrayList<>();
        }
        this.inits = inits;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" :.byte ");
        for (int iter = 0; iter < inits.size(); iter++) {
            sb.append(inits.get(iter));
            if (iter < inits.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}