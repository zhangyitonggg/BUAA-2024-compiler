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
    public boolean equals(Object o) {
        if (o instanceof Error) {
            Error other = (Error) o;
            return (this.line == other.getLine()) && (this.errorKey == other.getErrorKey());
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.line + " " + errorKey + "\n";
    }
}
