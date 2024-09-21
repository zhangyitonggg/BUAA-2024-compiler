package Utils;

import frontend.Lexer;
import frontend.Token;

import java.util.ArrayList;

public class ErrorLog {
    private static ErrorLog errorLog;
    private ArrayList<Error> errors = new ArrayList<>();
    
    private ErrorLog() {}
    
    public static ErrorLog getInstance() {
        if (null == errorLog) {
            errorLog = new ErrorLog();
        }
        return errorLog;
    }
    
    public int getErrorNum() {
        return errors.size();
    }
    
    public void addError(Error error) {
        this.errors.add(error);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Error error : errors) {
            sb.append(error.toString());
        }
        return sb.toString();
    }
}
