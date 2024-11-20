package backend;

import llvm.value.Value;

public class Note {
    private Value value;
    
    public Note(Value value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        String s = value.toString();
        
        String[] lines = s.split("\n");
        StringBuilder result = new StringBuilder();
        
        // 遍历每行，添加注释符
        for (String line : lines) {
            result.append("# ").append(line).append("\n");
        }
        
        return result.toString().trim(); // 去除末尾多余的换行符
    }
}
