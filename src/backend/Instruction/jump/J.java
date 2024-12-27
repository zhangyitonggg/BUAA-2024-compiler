package backend.Instruction.jump;

public class J extends JumpM {
    public J(String label) {
        super(label);
    }
    
    @Override
    public String toString() {
        return super.toString("j");
    }
}
