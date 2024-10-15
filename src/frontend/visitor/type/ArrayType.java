package frontend.visitor.type;

public class ArrayType implements SymType {
    private final SymType elmType;
    private final int size;
    
    public ArrayType(SymType elmType, int size) {
        this.elmType = elmType;
        this.size = size;
    }
}
