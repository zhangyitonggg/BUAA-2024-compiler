package Utils;

public class Error {
    private int line;
    private char errorKey;
    
    public Error(int line, char errorKey) {
        this.line = line;
        this.errorKey = errorKey;
    }
    
    public int getLine() {
        return line;
    }
    
    public char getErrorKey() {
        return errorKey;
    }
    
    @Override
    public String toString() {
        return this.line + " " + errorKey + "\n";
    }
}
