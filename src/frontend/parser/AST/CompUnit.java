package frontend.parser.AST;

import frontend.parser.AnyNode;

import java.util.ArrayList;

// 编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef
public class CompUnit implements AnyNode {
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;
    
    public CompUnit(ArrayList<Decl> decls, ArrayList<FuncDef> funcDefs, MainFuncDef mainFuncDef) {
        this.decls  = decls;
        this.funcDefs = funcDefs;
        this.mainFuncDef = mainFuncDef;
    }
    
    public ArrayList<Decl> getDecls() {
        return decls;
    }
    
    public ArrayList<FuncDef> getFuncDefs() {
        return funcDefs;
    }
    
    public MainFuncDef getMainFuncDef() {
        return mainFuncDef;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Decl decl : decls) {
            sb.append(decl.toString());
        }
        for (FuncDef funcDef : funcDefs) {
            sb.append(funcDef.toString());
        }
        sb.append(mainFuncDef.toString());
        sb.append("<CompUnit>\n");
        return sb.toString();
    }
}
