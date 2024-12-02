package backend.data;

import java.util.ArrayList;

/**
 * 需要有初始化，对应int
 * x: .word 5  # 分配 4 字节内存，初始化为 5
 * array: .word 1, 2, 3, 4  # 分配 16 字节内存，分别初始化为 1, 2, 3, 4
 */
public class WordData extends Data {
    private ArrayList<Integer> inits;
    
    public WordData(String name, ArrayList<Integer> inits) {
        super(name);
        if (inits == null) {
            inits = new ArrayList<>();
        }
        this.inits = inits;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" :.word ");
        for (int iter = 0; iter < inits.size(); iter++) {
            sb.append(inits.get(iter));
            if (iter < inits.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
