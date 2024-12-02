package llvm;

import llvm.value.notInstr.BasicBlock;

public class Loop {
    private final BasicBlock initBb;
    private final BasicBlock condBb;
    private final BasicBlock forwardBb;
    private final BasicBlock bodyBb;
    private final BasicBlock endBb;
    
    public Loop(BasicBlock initBb, BasicBlock condBb, BasicBlock forwardBb, BasicBlock bodyBb, BasicBlock endBb) {
        this.initBb = initBb;
        this.condBb = condBb;
        this.forwardBb = forwardBb;
        this.bodyBb = bodyBb;
        this.endBb = endBb;
    }
    
    public BasicBlock getInitBb() {
        return initBb;
    }
    
    public BasicBlock getCondBb() {
        return condBb;
    }
    
    public BasicBlock getForwardBb() {
        return forwardBb;
    }
    
    public BasicBlock getBodyBb() {
        return bodyBb;
    }
    
    public BasicBlock getEndBb() {
        return endBb;
    }
}
