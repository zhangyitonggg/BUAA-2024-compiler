package backend.data;

/**
 * .str: .asciiz "This is a string\n"
 */
public class AsciiData extends Data {
    private String literal;
    
    public AsciiData(String name, String literal) {
        super(name);
        this.literal = literal;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" : .asciiz ").append("\"");
        sb.append(literal);
        sb.append("\"");
        return sb.toString();
    }
}
