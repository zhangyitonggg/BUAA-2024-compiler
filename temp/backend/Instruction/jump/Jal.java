package backend.Instruction.jump;

public class Jal extends JumpM {
    public Jal(String label) {
        super(label);
    }
    
    @Override
    public String toString() {
        return super.toString("jal");
    }
}
