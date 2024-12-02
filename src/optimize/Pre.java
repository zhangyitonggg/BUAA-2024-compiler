package optimize;

import backend.Instruction.jump.J;
import llvm.Module;
import llvm.value.instr.Branch;
import llvm.value.instr.Instruction;
import llvm.value.instr.Jump;
import llvm.value.notInstr.BasicBlock;
import llvm.value.notInstr.Function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class Pre {
    /**
     * 目前用不到了
     * 求每个基本块的前驱和后继
     * @param module
     */
    public static void anaCFG(Module module) {
        ArrayList<Function> functions = module.getFunctions();
        for (Function function : functions) {
            function.calcCFG();
        }
    }
    
    /**
     * 分析支配
     * @param module
     */
    public static void anaDom(Module module) {
        ArrayList<Function> functions = module.getFunctions();
        for (Function function : functions) {
            function.calcCFG();
            function.calcDomByRel();
            function.calcImmeDom();
            function.calcDomFro();
//            for (BasicBlock bb : function.getBbs()) {
//                System.out.print(bb.getName() + ": ");
//                for (BasicBlock tmp : bb.domFro) {
//                    System.out.print(tmp.getName() + " ");
//                }
//                System.out.println(" ");
//            }
//            for (BasicBlock bb : function.getBbs()) {
//                System.out.print(bb.getName() + ": ");
//                if (bb.immeDomBy == null) {
//                    continue;
//                }
//                System.out.println(bb.immeDomBy.getName());
//            }
        }
    }
    
    public static void deleteDeadCode(Module module) {
        for (Function function : module.getFunctions()) {
            // 删除每一个bb里的死代码,这个我们通过新建bb解决了
            // 删除死的bb,也就是entry到不了的bb，这里我们只看指令，不看具体结果
            HashSet<BasicBlock> liveBbs = new HashSet<>();
            dfs2findLive(function.getFirstBb(), liveBbs);
            // 接下来开始删了
            ArrayList<Integer> indexs = new ArrayList<>();
            LinkedList<BasicBlock> bbs = function.getBbs();
            for (int i = 0; i < bbs.size(); i++) {
                if (!liveBbs.contains(bbs.get(i))) {
                    indexs.add(i);
                }
            }
            // 需要倒着来
            for (int i = indexs.size() - 1; i >= 0; i--) {
                function.deleteBb(indexs.get(i));
            }
        }
    }
    
    private static void dfs2findLive(BasicBlock bb, HashSet<BasicBlock> liveBbs) {
        if (!liveBbs.contains(bb)) {
            // 只有不包含时才有意义做下去
            liveBbs.add(bb);
            Instruction end = bb.getInstrAtEnd();
            if (end instanceof Branch branch) {
                BasicBlock trueBb = (BasicBlock) branch.getOperand(1);
                BasicBlock falseBb = (BasicBlock) branch.getOperand(2);
                dfs2findLive(trueBb, liveBbs);
                dfs2findLive(falseBb, liveBbs);
            } else if (end instanceof Jump jump) {
                BasicBlock toBb = (BasicBlock) jump.getOperand(0);
                dfs2findLive(toBb, liveBbs);
            }
        }
    }
}
