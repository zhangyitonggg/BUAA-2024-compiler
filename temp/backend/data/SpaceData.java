package backend.data;

/**
 * buffer: .space 64  # 分配64字节空间，未初始化
 */
public class SpaceData extends Data {
    private int byteNum;
    
    public SpaceData(String name, int byteNum) {
        super(name);
        this.byteNum = byteNum;
    }
    
    public int getByteNum() {
        return byteNum;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" :.space ");
        sb.append(byteNum);
        return sb.toString();
    }
}
