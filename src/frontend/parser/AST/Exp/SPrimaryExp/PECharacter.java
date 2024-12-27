package frontend.parser.AST.Exp.SPrimaryExp;

import frontend.parser.AST.Exp.Character_;
import frontend.parser.AnyNode;

public class PECharacter implements PrimaryExp, AnyNode {
    private Character_ character;
    
    public PECharacter(Character_ character) {
        this.character = character;
    }
    
    public int getAscii() {
        return character.getAscii();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(character.toString());
        sb.append("<PrimaryExp>\n");
        return sb.toString();
    }
}
