package backend.Register;

import java.util.HashMap;

public class Reg {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 静态区 */
    public static Reg getArgReg(int index) {
        Reg res = null;
        switch (index){
            case 0 -> res = a0;
            case 1 -> res = a1;
            case 2 -> res = a2;
            case 3 -> res = a3;
        }
        return res;
    }
   
    public static int getIndex(Reg reg) {
        return reg.index;
    }
   
    private static final HashMap<Integer, String> index2name = new HashMap<>() {{
        // constant 0;
        put(0, "zero");
        // 留给汇编器
        put(1, "at");
        // 函数返回值
        put(2, "v0");
        // 函数返回地址
        put(31, "ra");
        // 栈指针
        put(29, "sp");
        // 函数参数
        put(4, "a0");
        put(5, "a1");
        put(6, "a2");
        put(7, "a3");
        // 局部寄存器
        put(8, "t0");
        put(9, "t1");
        put(10, "t2");
        put(11, "t3");
        put(12, "t4");
        put(13, "t5");
        put(14, "t6");
        put(15, "t7");
        put(24, "t8");
        put(25, "t9");
        // 全局寄存器
        put(16, "s0");
        put(17, "s1");
        put(18, "s2");
        put(19, "s3");
        put(20, "s4");
        put(21, "s5");
        put(22, "s6");
        put(23, "s7");
        
        // 辅助寄存器，手动分配任务
        put(26, "k0");
        put(27, "k1");
        // todo 可以挪作他用
        put(3, "v1");

        put(28, "gp");
        put(30, "fp");
    }};
    public final static Reg zero = new Reg(0, "zero");
    public final static Reg at = new Reg(1, "at");
    public final static Reg v0 = new Reg(2, "v0");
    public final static Reg ra = new Reg(31, "ra");
    public final static Reg sp = new Reg(29, "sp");
    public final static Reg a0 = new Reg(4, "a0");
    public final static Reg a1 = new Reg(5, "a1");
    public final static Reg a2 = new Reg(6, "a2");
    public final static Reg a3 = new Reg(7, "a3");
    public final static Reg t0 = new Reg(8, "t0");
    public final static Reg t1 = new Reg(9, "t1");
    public final static Reg t2 = new Reg(10, "t2");
    public final static Reg t3 = new Reg(11, "t3");
    public final static Reg t4 = new Reg(12, "t4");
    public final static Reg t5 = new Reg(13, "t5");
    public final static Reg t6 = new Reg(14, "t6");
    public final static Reg t7 = new Reg(15, "t7");
    public final static Reg t8 = new Reg(24, "t8");
    public final static Reg t9 = new Reg(25, "t9");
    public final static Reg s0 = new Reg(16, "s0");
    public final static Reg s1 = new Reg(17, "s1");
    public final static Reg s2 = new Reg(18, "s2");
    public final static Reg s3 = new Reg(19, "s3");
    
    public final static Reg s4 = new Reg(20, "s4");
    public final static Reg s5 = new Reg(21, "s5");
    public final static Reg s6 = new Reg(22, "s6");
    public final static Reg s7 = new Reg(23, "s7");
    
    public final static Reg v1 = new Reg(3, "v1");
    public final static Reg k0 = new Reg(26, "k0");
    public final static Reg k1 = new Reg(27, "k1");
    public final static Reg gp = new Reg(28, "gp");
    public final static Reg fp = new Reg(30, "fp");
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 非静态区 */
    private final int index;
    private final String name;
    
    private Reg(int index, String name) {
        this.index = index;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "$" + name;
    }
}
