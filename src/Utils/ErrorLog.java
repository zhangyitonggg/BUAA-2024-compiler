package Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        // tm竟然需要按行数输出，浪费我一晚上生命。
        Collections.sort(errors, Comparator.comparingInt(Error::getLine));
        StringBuilder sb = new StringBuilder();
        Error lastError = null;
        for (Error error : errors) {
            // 取消重复的
            if (lastError == null || !lastError.equals(error)) {
                sb.append(error.toString());
                lastError = error;
            }
        }
        return sb.toString();
    }
}
