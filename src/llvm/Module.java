package llvm;

import llvm.value.notInstr.Function;
import llvm.value.notInstr.GlobalVar;
import llvm.value.notInstr.StringLiteral;

import java.util.ArrayList;
import java.util.HashMap;

public class Module {
    private final ArrayList<StringLiteral> stringLiterals = new ArrayList<>();
    private final ArrayList<GlobalVar> globalVars = new ArrayList<>();
    private final ArrayList<Function> functions = new ArrayList<>(); // 这里包含Main函数，并且在最后
    
    public Module() {
    
    }
    
    public void addStringLiteral(StringLiteral stringLiteral) {
        stringLiterals.add(stringLiteral);
    }
    
    public void addGlobalVar(GlobalVar globalVar) {
        globalVars.add(globalVar);
    }
    
    public void addFunctions(Function function) {
        functions.add(function);
    }
    
    public ArrayList<StringLiteral> getStringLiterals() {
        return this.stringLiterals;
    }
    
    public ArrayList<GlobalVar> getGlobalVars() {
        return this.globalVars;
    }
    
    public ArrayList<Function> getFunctions() {
        return this.functions;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // 输出函数声明
        sb.append("declare i32 @getint()").append("\n");
        sb.append("declare i32 @getchar()").append("\n");
        sb.append("declare void @putint(i32)").append("\n");
        sb.append("declare void @putch(i8)").append("\n");
        sb.append("declare void @putstr(i8*)").append("\n");
        sb.append("\n\n");
        // 输出全局字面量
        for (StringLiteral stringLiteral : stringLiterals) {
            sb.append(stringLiteral.toString());
            sb.append("\n");
        }
        sb.append("\n");
        // 输出全局变量
        for (GlobalVar globalVar : globalVars) {
            sb.append(globalVar.toString());
            sb.append("\n");
        }
        sb.append("\n");
        // 输出函数（最后一个为主函数）
        for (Function function : functions) {
            sb.append(function.toString());
            sb.append("\n");
            sb.append("\n");
        }
        return sb.toString();
    }
}
