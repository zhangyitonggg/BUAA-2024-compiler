clang -S -emit-llvm main.c -o main_no_opt.ll -O0    # 生成 LLVM ir (不开优化)

clang -S -emit-llvm main.m -o main_opt.ll -Os    # 生成 LLVM ir (中端优化)

clang -S main.c -o main_no_opt.s                    # 生成汇编

clang -S main.bc -o main_opt.s -Os               # 生成汇编（后端优化）

clang -ccc-print-phases main.c               # 查看编译的过程

clang -E -Xclang -dump-tokens main.c         # 生成 tokens（词法分析）

clang -fsyntax-only -Xclang -ast-dump main.c # 生成抽象语法树

clang -c main.c -o main.o                    # 生成目标文件

clang main.c -o main                         # 直接生成可执行文件

