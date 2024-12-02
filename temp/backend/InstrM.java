package backend;

import Utils.Config;

import java.io.ObjectInputFilter;

/**
 * 这只是一个顶层的父类
 */
public class InstrM {
    private Note note;
    
    public void setNote(Note note) {
        this.note = note;
    }
    
    public String getNote() {
        if (Config.printCommentInMips && note != null) {
            return "   " + note.toString() + "\n";
        }
        return "";
    }
}
