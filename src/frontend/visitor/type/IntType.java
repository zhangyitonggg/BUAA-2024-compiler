package frontend.visitor.type;

public class IntType implements SymType {
    private static IntType c;
    
    private IntType(){
    
    }
    
    public IntType getInt() {
        if (c == null) {
            c = new IntType();
        }
        return c;
    }
}
