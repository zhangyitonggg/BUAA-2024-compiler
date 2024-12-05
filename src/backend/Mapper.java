package backend;

import backend.Instruction.assign.Li;
import backend.Instruction.branch.Beq;
import backend.Instruction.jump.J;
import backend.Instruction.memory.MemoryM;
import backend.Register.Reg;
import llvm.Module;
import llvm.types.DataIrTy;
import llvm.types.IrTy;
import llvm.types.PointerIrTy;
import llvm.value.Value;
import llvm.value.constant.ConstData;
import llvm.value.constant.Constant;
import llvm.value.instr.*;
import llvm.value.notInstr.*;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 第一个char四字节对齐，也就是说只有连续的char数组其中的从第二个元素到最后一个元素没有四字节对齐要求
 */

public class Mapper extends MipsFactory {
    private static Mapper mapper;
    
    private Mapper() {
        super();
    }
    
    public static Mapper getInstance() {
        if (mapper == null) {
            mapper = new Mapper();
        }
        return mapper;
    }
    
    public String map(Module module) {
        ArrayList<StringLiteral> stringLiterals = module.getStringLiterals();
        ArrayList<GlobalVar> globalVars = module.getGlobalVars();
        ArrayList<Function> functions = module.getFunctions();
        
        for (StringLiteral stringLiteral : stringLiterals) {
            mapStringLiteral(stringLiteral);
        }
        for (GlobalVar globalVar : globalVars) {
            mapGlobalVar(globalVar);
        }
        // 先映射一下主函数，保证mips代码区一开始就是主函数
        mapFunction(functions.get(functions.size() - 1));
        for (int i = 0; i < functions.size() - 1; i++) {
            mapFunction(functions.get(i));
        }
        
        return super.toString();
    }

    
    protected void mapFunction(Function function) {
        // 完成一些预备工作，生成标签以及更新相关全局变量
        beginFunction(function);
        // 传参数，前四个放到a0,a1,a2,a3.其他的压栈。
        ArrayList<FParam> fParams = function.getFParams();
        for (int iter = 0; iter < fParams.size(); iter+=1) {
            if (iter <  4) {
                putReg(fParams.get(iter), Reg.getArgReg(iter));
            }
            // 栈顶实际上应该是已经被用了，所以应该上来就减
            DataIrTy type = (DataIrTy) fParams.get(iter).getType();
            int byteNum = type.getByte();
            // 传参时应当遵守四字节对齐的约定
            // int align = type.getAlign();
            subAlign(byteNum, 4);
            putOffset(fParams.get(iter), curOffset);
        }
        // 为所有指令分配寄存器或者压栈，当然，目前只有压栈
        for (Instruction instruction : function.getAllInstr()) {
            if (instruction instanceof Copy copy && findOffset(copy.getTarget()) == null && findReg(copy.getTarget()) == null) {
                int byteNum = copy.getTarget().getType().getByte();
                subAlign(byteNum, 4);
                putOffset(copy.getTarget(), curOffset);
            } else if (instruction.getName() != null && findOffset(instruction) == null && findReg(instruction) == null) {
                int byteNum = instruction.getType().getByte();
                // 传参时应当遵守四字节对齐的约定
                // int align = instruction.getType().getAlign();
                subAlign(byteNum, 4);
                putOffset(instruction, curOffset);
            }
        }
        // 接下来就是真正的生成mips指令的环节了，也就是一次映射各个基本块
        LinkedList<BasicBlock> bbs = function.getBbs();
        for (BasicBlock bb : bbs) {
            mapBlock(bb);
        }
    }
    
    protected void mapStringLiteral(StringLiteral stringLiteral) {
        // 去掉”@“
        makeAsciiData(stringLiteral.getName().substring(1), stringLiteral.getOriLiteral());
    }
    
    protected void mapGlobalVar(GlobalVar globalVar) {
        String name = globalVar.getName().substring(1);
        Constant init = globalVar.getInit();
        ArrayList<Integer> initNums = init.getAllNum();
        boolean hasChar = init.hasChar();
        boolean isZero = true;
        for (Integer t : initNums) {
            if (t != 0) {
                isZero = false;
                break;
            }
        }
        
        if (isZero) {
            int length = initNums.size();
            int byteNum = hasChar ? length : length * 4;
            makeSpaceData(name, byteNum);
        } else {
            if (hasChar) {
                makeByteData(name, initNums);
            } else {
                makeWordData(name, initNums);
            }
        }
    }
    
    protected void mapBlock(BasicBlock bb) {
        makeLabel(bb.getName().substring(1));
        LinkedList<Instruction> instructions = bb.getAllInstr();
        for (int i = 0; i < instructions.size(); i++) {
            mapInstruction(instructions.get(i));
        }
    }
    
    protected void mapInstruction(Instruction instruction) {
        if (instruction instanceof Copy copy) {
            mapCopy(copy);
        } else if (instruction instanceof Compute compute) {
            mapCompute(compute);
        } else if (instruction instanceof Alloca alloca) {
            mapAlloca(alloca);
        }  else if (instruction instanceof Call call) {
            mapCall(call);
        } else if (instruction instanceof Getelementptr getelementptr) {
            mapGetelementptr(getelementptr);
        } else if (instruction instanceof Icmp icmp) {
            mapIcmp(icmp);
        } else if (instruction instanceof Ret ret) {
            mapRet(ret);
        } else if (instruction instanceof Zext zext) {
            mapZext(zext);
        } else if (instruction instanceof Trunc trunc) {
            mapTrunc(trunc);
        } else if (instruction instanceof Branch branch) {
            mapBranch(branch);
        } else if (instruction instanceof Jump jump) {
            mapJump(jump);
        } else if (instruction instanceof Store store) {
            mapStore(store);
        } else if (instruction instanceof Load load) {
            mapLoad(load);
        } else {
            System.out.println("What happened!!!");
        }
    }
    
    public void mapCopy(Copy copy) {
        // 准备工作
        Value from = copy.getFrom();
        Value target = copy.getTarget();
        Reg fromReg = findReg(from);
        Reg targetReg = findReg(target);
        if (targetReg == null) {
            targetReg = Reg.k0;
        }
        
        if (from instanceof ConstData fromConstData) {
            makeLi(targetReg, fromConstData.getValue());
        } else if (fromReg != null) {
            makeMove(targetReg, fromReg);
        } else {
            // 似乎这里用from和target没任何区别
            makeLoad(from.getType().getAlign(), targetReg, findOffset(from), Reg.sp);
        }
        
        if (findReg(target) == null) {
            makeStore(target.getType().getAlign(), targetReg, findOffset(target), Reg.sp);
        }
    }
    
    public void mapCompute(Compute compute) {
        Compute.Op op = compute.getOp();
        Value vLeft = compute.getOperand(0);
        Value vRight = compute.getOperand(1);
        
        Reg mLeft = Reg.k0;
        Reg mRight = Reg.k1;
        
        if (vLeft instanceof ConstData vLeftConst) {
            makeLi(mLeft, vLeftConst.getValue());
        } else if(findReg(vLeft) != null) {
            mLeft = findReg(vLeft);
        } else {
            // 只有int有资格参与运算
            makeLoad(vLeft.getType().getAlign(), mLeft, findOffset(vLeft), Reg.sp);
        }
        if (vRight instanceof ConstData vRightConst) {
            makeLi(mRight, vRightConst.getValue());
        } else if(findReg(vRight) != null) {
            mRight = findReg(vRight);
        } else {
            // 只有int有资格参与运算
            makeLoad(vRight.getType().getAlign(), mRight, findOffset(vRight), Reg.sp);
        }
        
        if (findReg(compute) != null) {
            makeCompute(compute, findReg(compute), mLeft, mRight);
        } else {
            makeCompute(compute, Reg.k0, mLeft, mRight);
            makeStore(compute.getType().getAlign(), Reg.k0, findOffset(compute), Reg.sp);
        }
    }
    
    public void mapAlloca(Alloca alloca) {
        // alloca必然是指针
        IrTy deRefIrTy = ((PointerIrTy) alloca.getType()).deRefIrTy;
        int byteNum = deRefIrTy.getByte(); // 这里可能是1，可能是4，还可能更多
        // int align = deRefIrTy.getAlign();
        subAlign(byteNum, 4);
        if (findReg(alloca) != null) {
            // 存储数据地址
            Li li = makeLi(findReg(alloca), curOffset);
            li.setNote(new Note(alloca));
            makeCompute(Compute.Op.ADD, findReg(alloca), Reg.sp, findReg(alloca));
        } else {
            // 先用k0存储数据地址
            Li li = makeLi(Reg.k0, curOffset);
            li.setNote(new Note(alloca));
            makeCompute(Compute.Op.ADD, Reg.k0, Reg.sp, Reg.k0);
            // 存到栈上
            makeStore(alloca.getType().getAlign(), Reg.k0, findOffset(alloca), Reg.sp);
        }
    }
    
    public void mapCall(Call call) {
        int num = instrList.size();
        Function function = call.getFunction();
        String name = function.getName().substring(1);
        if (name.equals("getint") || name.equals("getchar")) {
            Li li;
            if (name.equals("getint")) {
                li = makeLi(Reg.v0, 5);
            } else {
                li = makeLi(Reg.v0, 12);
            }
            makeSyscall();
            if (findReg(call) != null) {
                makeMove(findReg(call), Reg.v0);
            } else {
                // 这两个函数的返回值都是4字节对齐的
                makeStore(function.getReturnType().getAlign(), Reg.v0, findOffset(call), Reg.sp);
            }
        } else if (name.equals("putint") || name.equals("putch")) {
            // 先保存a0
            makeMove(Reg.k1, Reg.a0);
            // 有且仅有一个i32的参数
            Value arg = call.getOperand(1);
            if (arg instanceof ConstData constData) {
                Li li = makeLi(Reg.a0, constData.getValue());
            } else if (findReg(arg) != null) {
                // 感觉这里如果两个寄存器相同就没必要赋值了
                makeMove(Reg.a0, findReg(arg));
            } else {
                // 一定是4
                makeLoad(arg.getType().getAlign(), Reg.a0, findOffset(arg), Reg.sp);
            }
            if (name.equals("putint")) {
                makeLi(Reg.v0, 1);
            } else {
                makeLi(Reg.v0, 11);
            }
            makeSyscall();
            // 恢复a0
            makeMove(Reg.a0, Reg.k1);
        } else if (name.equals("putstr")) {
            // 先保存a0
            makeMove(Reg.k1, Reg.a0);
            Value arg = call.getOperand(1);
            if (arg instanceof StringLiteral) {
                makeLa(Reg.a0, arg.getName().substring(1));
            } else if (findReg(arg) != null){
                makeMove(Reg.a0, findReg(arg));
            } else {
                makeLoad(arg.getType().getAlign(), Reg.a0, findOffset(arg), Reg.sp);
            }
            makeLi(Reg.v0, 4);
            makeSyscall();
            // 恢复a0
            makeMove(Reg.a0, Reg.k1);
        } else {
            /*
                需要存什么：
                1. 被使用的寄存器，这很显然,里面包括全局寄存器以及a0啥的。
                2. ra寄存器
             */
            ArrayList<Reg> usedReg = new ArrayList<>(value2reg.values());
            // 搞明白存啥了，接下来就存就行了
            for (int iter = 0; iter < usedReg.size(); iter += 1) {
                int offset = curOffset - 4 * (iter + 1);
                Reg src = usedReg.get(iter);
                makeStore(findValueWithReg(src).getType().getAlign(), src, offset, Reg.sp);
            }
            // 算是一个特例吧，地址当然是4字节的
            makeStore(4, Reg.ra, curOffset - usedReg.size() * 4 - 4, Reg.sp);
            /*
                处理实参：
                1. 前四个参数，存到a0,a1,a2,a3中
                2. 其余参数，压栈
             */
            ArrayList<Value> rParams = call.getRParams();
            for (int i = 0; i < rParams.size(); i+=1) {
                Value rParam = rParams.get(i);
                if (i < 4) {
                    // 前四个参数，存到a0,a1,a2,a3
                    if (rParam instanceof ConstData rParamConst) {
                        // 常数，这是必需有的
                        makeLi(Reg.getArgReg(i), rParamConst.getValue());
                    } else {
                        // 非常数，也就是说在寄存器或者栈上存着呢
                        if (findReg(rParam) != null) {
                            // 寄存器上有，但这并不意味着寄存器上的值就是正确的
                            // 这里有一个非常棘手的问题，就是这个寄存器可能是a0,a1,a2,a3
                            Reg from = findReg(rParam);
                            int index = Reg.getIndex(from);
                            if (from == Reg.getArgReg(i)) {
                                // 啥都不用干
                            } else if (index <= 7 && index >= 4 && index < i + 4) {
                                // 这对应的要写入a1，但是需要从a0取值的情况
                                int offset = curOffset - (usedReg.indexOf(from) + 1) * 4;
                                makeLoad(findValueWithReg(from).getType().getAlign(), Reg.getArgReg(i), offset, Reg.sp);
                            } else {
                                // 这种情况下可以直接用move
                                makeMove(Reg.getArgReg(i), from);
                            }
                        } else {
                            // 寄存器上没有，直接去栈上找
                            int offset = findOffset(rParam);
                            makeLoad(rParam.getType().getAlign(), Reg.getArgReg(i), offset, Reg.sp);
                        }
                    }
                } else {
                    // 不是前四个参数，需要存栈上
                    int offset = curOffset - 4 * usedReg.size() - 4 - (i + 1) * 4;
                    if (rParam instanceof ConstData rParamConst) {
                        // 常数，这是必需有的
                        makeLi(Reg.k0, rParamConst.getValue());
                        makeStore(rParam.getType().getAlign(), Reg.k0, offset, Reg.sp);
                    } else {
                        // 非常数
                        if (findReg(rParam) != null) {
                            // 寄存器上有，还是应该分情况
                            Reg from = findReg(rParam);
                            int index = Reg.getIndex(from);
                            if (index <= 7 && index >= 4) {
                                // 是ax寄存器，已经覆盖了
                                int loadOffset = curOffset - (usedReg.indexOf(from) + 1) * 4;
                                makeLoad(findValueWithReg(from).getType().getAlign(), Reg.k0, loadOffset, Reg.sp);
                                makeStore(rParam.getType().getAlign(), Reg.k0, offset, Reg.sp);
                            } else {
                                makeStore(rParam.getType().getAlign(), from, offset, Reg.sp);
                            }
                        } else {
                            // 寄存器上没有，需要去栈上找
                            int loadOffset = findOffset(rParam);
                            makeLoad(rParam.getType().getAlign(), Reg.k0, loadOffset, Reg.sp);
                            makeStore(rParam.getType().getAlign(), Reg.k0, offset, Reg.sp);
                        }
                    }
                }
            }
            /*
              开始跳转，这里需要做三件事情：
              1. 更新sp
              2. jal
              3. 还原ra
              4. 还原sp
             */
            makeLi(Reg.k0, curOffset - usedReg.size() * 4 - 4);
            makeCompute(Compute.Op.ADD, Reg.sp, Reg.sp, Reg.k0);
            makeJal(name);
             // 算是一个特例吧，地址当然是4字节的。其实是对应上面那个特例
            makeLoad(4, Reg.ra, 0, Reg.sp);
            makeLi(Reg.k0, -1 * (curOffset - usedReg.size() * 4 - 4));
            makeCompute(Compute.Op.ADD, Reg.sp, Reg.sp, Reg.k0);
            // 跳转完成后，恢复被使用的寄存器
            for (int iter = 0; iter < usedReg.size(); iter+=1) {
                int offset = curOffset - 4 * (iter + 1);
                Reg dst = usedReg.get(iter);
                makeLoad(findValueWithReg(dst).getType().getAlign(), dst, offset, Reg.sp);
            }
            // 处理返回值
            // 应当在回复寄存器后再处理返回值，否则可能会被覆盖
            if (call.hasReturnValue()) {
                // 存在返回值
                if (findReg(call) != null) {
                    // 存到特定寄存器之中
                    makeMove(findReg(call), Reg.v0);
                } else {
                    // 存到栈上去
                    makeStore(call.getType().getAlign(), Reg.v0, findOffset(call), Reg.sp);
                }
            }
        }
        instrList.get(num).setNote(new Note(call));
    }
    
    public void mapGetelementptr(Getelementptr getelementptr) {
        int num = instrList.size();
        // 准备工作
        Reg baseReg = Reg.k0;
        Reg indexReg = Reg.k1;
        Value baseValue = getelementptr.getBase();
        Value indexValue = getelementptr.getIndex();
        // 先解析baseValue，目标是将地址存到baseReg之中
        if (baseValue instanceof StringLiteral || baseValue instanceof GlobalVar) {
            makeLa(baseReg, baseValue.getName().substring(1));
        } else if (findReg(baseValue) != null) {
            baseReg = findReg(baseValue);
        } else {
            // baseValue一定是指针类型的，所以一定是4，为了形式上的统一，还是保持原样
            makeLoad(baseValue.getType().getAlign(), baseReg, findOffset(baseValue), Reg.sp);
        }
        // 解析indexValue，目标是将偏移量存到indexReg中
        DataIrTy deReIrTy = (DataIrTy) getelementptr.getType().deRefIrTy;
        int tmp = deReIrTy.isI8() ? 1 : 4;
        if (indexValue instanceof ConstData indexValueConst) {
            makeLi(indexReg, indexValueConst.getValue() * tmp);
        } else if (findReg(indexValue) != null) {
            if (tmp == 4) {
                // 这时候indexReg还是一个保持是k1
                makeSll(indexReg, findReg(indexValue), 2);
            } else {
                indexReg = findReg(indexValue);
            }
        } else {
            // 必然是I32
            int offset = findOffset(indexValue);
            makeLoad(indexValue.getType().getAlign(), indexReg, offset, Reg.sp);
            if (tmp == 4) {
                makeSll(indexReg, indexReg, 2);
            }
        }
        // 将baseReg和indexReg相加，存到合适寄存器or栈地址
        if (findReg(getelementptr) != null) {
            makeCompute(Compute.Op.ADD, findReg(getelementptr), baseReg, indexReg);
        } else {
            makeCompute(Compute.Op.ADD, Reg.k0, baseReg, indexReg);
            // 必然是4
            makeStore(getelementptr.getType().getAlign(), Reg.k0, findOffset(getelementptr), Reg.sp);
        }
        instrList.get(num).setNote(new Note(getelementptr));
    }
    
    public void mapIcmp(Icmp icmp) {
        int num = instrList.size();
        // 准备工作
        Reg leftReg = Reg.k0;
        Reg rightReg = Reg.k1;
        Value leftValue = icmp.getOperand(0);
        Value rightValue = icmp.getOperand(1);
        // 解析leftValue，rightValue
        if (leftValue instanceof ConstData leftValueConst) {
            makeLi(leftReg, leftValueConst.getValue());
        } else if (findReg(leftValue) != null) {
            leftReg = findReg(leftValue);
        } else {
            // 理论上来说一定是i32，因为只有i32有资格参与计算
            makeLoad(leftValue.getType().getAlign(), leftReg, findOffset(leftValue), Reg.sp);
        }
        if (rightValue instanceof ConstData rightValueConst) {
            makeLi(rightReg, rightValueConst.getValue());
        } else if (findReg(rightValue) != null) {
            rightReg = findReg(rightValue);
        } else {
            // 理论上来说一定是i32，因为只有i32有资格参与计算
            makeLoad(rightValue.getType().getAlign(), rightReg, findOffset(rightValue), Reg.sp);
        }
        // 比较大小
        if (findReg(icmp) == null) {
            // 未分配到寄存器
            makeCompare(icmp.getOp(), Reg.k0, leftReg, rightReg);
            // icmp类型为i1, i1的align为1字节
            makeStore(icmp.getType().getAlign(), Reg.k0, findOffset(icmp), Reg.sp);
        } else {
            // 分到了寄存器
            Reg target = findReg(icmp);
            makeCompare(icmp.getOp(), target, leftReg, rightReg);
        }
        instrList.get(num).setNote(new Note(icmp));
    }
    
    public void mapTrunc(Trunc trunc) {
        int num = instrList.size();
        // 准备工作
        Reg tmp = Reg.k0;
        Value oriValue = trunc.getOriValue();
        // 得到tmp寄存器
        if (oriValue instanceof ConstData oriValueConst) {
            // 真的会存在这种可能吗
            makeLi(tmp, oriValueConst.getValue());
        } else if (findReg(oriValue) != null) {
            tmp = findReg(oriValue);
        } else {
            makeLoad(oriValue.getType().getAlign(), tmp, findOffset(oriValue), Reg.sp);
        }
        // 实际上就是从一个寄存器复制到另一个寄存器 or 栈
        Reg target = findReg(trunc);
        if (target == null) {
            // 存到栈上
            // 也就是截断后的目标类型的align
            makeStore(trunc.getType().getAlign(), tmp, findOffset(trunc), Reg.sp);
        } else if (target != tmp) {
            // 二者不同时才有复制的必要
            makeMove(target, tmp);
        }
        // 决定是否加注释
        if (num < instrList.size()) {
            instrList.get(num).setNote(new Note(trunc));
        }
    }
    
    public void mapZext(Zext zext) {
        int num = instrList.size();
        // 准备工作
        Reg tmp = Reg.k0;
        Value oriValue = zext.getOriValue();
        // 得到tmp寄存器
        if (oriValue instanceof ConstData oriValueConst) {
            // 真的会存在这种可能吗
            makeLi(tmp, oriValueConst.getValue());
        } else if (findReg(oriValue) != null) {
            tmp = findReg(oriValue);
        } else {
            // 必然是1，否则没道理扩展
            makeLoad(oriValue.getType().getAlign(), tmp, findOffset(oriValue), Reg.sp);
        }
        // 实际上就是从一个寄存器复制到另一个寄存器 or 栈
        Reg target = findReg(zext);
        if (target == null) {
            // 存到栈上
            // 也就是扩展后的目标类型的align
            makeStore(zext.getType().getAlign(), tmp, findOffset(zext), Reg.sp);
        } else if (target != tmp) {
            // 二者不同时才有复制的必要
            makeMove(target, tmp);
        }
        // 决定是否加注释
        if (num < instrList.size()) {
            instrList.get(num).setNote(new Note(zext));
        }
    }
    
    public void mapJump(Jump jump) {
        J j = makeJ(jump.getOperand(0).getName().substring(1));
        j.setNote(new Note(jump));
    }
    
    /**
     * 使用beq指令+j指令：
     * 当cond等于0的时候，跳转到falseLabel
     * 否则，用J指令跳转到trueLabel
     * todo 这里是有比较大的优化空间的，可以将icmp的映射放到这边进行，因此有可能将branch换成jump
     * @param branch
     */
    public void mapBranch(Branch branch) {
        Value cond = branch.getOperand(0);
        Value trueBb = branch.getOperand(1);
        Value falseBb = branch.getOperand(2);
        String trueLabel = trueBb.getName().substring(1);
        String falseLabel = falseBb.getName().substring(1);
        if (cond instanceof ConstData condConst) {
            if (condConst.getValue() != 0) {
                makeJ(trueLabel);
            } else {
                makeJ(falseLabel);
            }
        } else if (findReg(cond) == null) {
            // cond 需要往栈上找
            MemoryM m = makeLoad(cond.getType().getAlign(), Reg.k0, findOffset(cond), Reg.sp);
            m.setNote(new Note(branch));
            makeBeq(Reg.k0, Reg.zero, falseLabel);
            makeJ(trueLabel);
        } else {
            // cond 就在通用寄存器里
            Beq beq = makeBeq(findReg(cond), Reg.zero, falseLabel);
            beq.setNote(new Note(branch));
            makeJ(trueLabel);
        }
    }
    
    public void mapStore(Store store) {
        int num = instrList.size();
        // 准备工作
        Value value = store.getOperand(0);
        Value pointer = store.getOperand(1);
        Reg data = Reg.k0;
        Reg addr = Reg.k1;
        // 在addr寄存器里存储pointer的具体的值
        if (pointer instanceof StringLiteral || pointer instanceof GlobalVar) {
            String label = pointer.getName().substring(1);
            makeLa(addr, label);
        } else if (findReg(pointer) == null) {
            // align一定是4，因为pointer是指针
            makeLoad(pointer.getType().getAlign(), addr, findOffset(pointer), Reg.sp);
        } else {
            addr = findReg(pointer);
        }
        // 寻找data
        if (value instanceof ConstData valueConst) {
            makeLi(data, valueConst.getValue());
        } else if (findReg(value) == null) {
            makeLoad(value.getType().getAlign(), data, findOffset(value), Reg.sp);
        } else {
            data = findReg(value);
        }
        // 将data填到内存中
        makeStore(value.getType().getAlign(), data, 0, addr);
        // 回填注释
        instrList.get(num).setNote(new Note(store));
    }
    
    public void mapLoad(Load load) {
        int num = instrList.size();
        // 准备工作
        Reg addr = Reg.k0;
        Value pointer = load.getOperand(0);
        // 在addr寄存器里存储pointer的具体的值
        if (pointer instanceof StringLiteral || pointer instanceof GlobalVar) {
            String label = pointer.getName().substring(1);
            makeLa(addr, label);
        } else if (findReg(pointer) == null) {
            // align一定是4，因为pointer是指针
            makeLoad(pointer.getType().getAlign(), addr, findOffset(pointer), Reg.sp);
        } else {
            addr = findReg(pointer);
        }
        // 依据addr寄存器中的地址去访问内存，并将数据存到合适的地方
        int align = load.getType().getAlign();
        Reg target = findReg(load);
        if (target == null) {
            makeLoad(align, Reg.k0, 0, addr);
            makeStore(align, Reg.k0, findOffset(load), Reg.sp);
        } else {
            makeLoad(align, target, 0, addr);
        }
        // 回填注释
        instrList.get(num).setNote(new Note(load));
    }
    
    public void mapRet(Ret ret) {
        int num = instrList.size();
        if (curFunc.getName().equals("@main")) {
            // 主函数
            makeLi(Reg.v0, 10);
            makeSyscall();
        } else {
            // 其他函数
            if (ret.hasReturnValue()) {
                // 有返回值, 放到v0中
                Value returnValue = ret.getOperand(0);
                if (returnValue instanceof ConstData returnValueConst) {
                    makeLi(Reg.v0, returnValueConst.getValue());
                } else if (findReg(returnValue) != null) {
                    makeMove(Reg.v0, findReg(returnValue));
                } else {
                    makeLoad(returnValue.getType().getAlign(), Reg.v0, findOffset(returnValue), Reg.sp);
                }
                makeJr(Reg.ra);
            } else {
                // 无返回值
                makeJr(Reg.ra);
            }
        }
        instrList.get(num).setNote(new Note(ret));
    }
}
