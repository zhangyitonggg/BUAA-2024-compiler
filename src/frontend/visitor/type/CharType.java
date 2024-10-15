package frontend.visitor.type;

public class CharType implements SymType {
    private static CharType c;
    
    private CharType(){
    
    }
    
    public CharType getChar() {
        if (c == null) {
            c = new CharType();
        }
        return c;
    }
}
