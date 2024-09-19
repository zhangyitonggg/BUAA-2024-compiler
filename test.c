#include<stdio.h>
// 呃呃。。。编译好难。。。/****我是不是应该去玩一下呢、、 // 玩玩玩、、 、、// ww要挂科了*/
const int con_num_1 = 1, con_num_2 = 2, con_nums_1[3] = {1, 2, 1 + 2};
const char con_ch_1 = 'z'; 
int count = 0;
int num_1, num_2 = 2, nums_1[3] = {4, 5, 6};
char ch1 /*//;//*/;
char ch2 = 'a';
const int error = -100000;



int getchar() {
    char c;
    scanf("%c", &c);
    return (int)c;
}

int getint() {
    int t;
    scanf("%d", &t);
    while(getchar() != '\n');
    return t;
}

void func1() {
    return;
}

void hello() {
    int  int2  =114 ;
    char char1 = '2';
    int c = int2 * char1   % 514;
    // printf("%d\n",   c); return0
    /*
     */
    //
    int d = +33370;
    if (c == 46) {
        int e = d / 10;
        int f = e;
        printf("22373337\n");
        printf("%d is zyt\n", e); /* 猜猜我是谁 */
    } 
    count += 2;
    int func1s;
    func1();
}

int sum(int a, int b) {
    int c = 1== 1;
    if (c != 1) {
        a = (a+ 1);
    } else {
        a = a +b- b;
    }
    count += 3;
    // printf("a = %d, b = %d\n", a, b);
    return a + b +  b - b + b - b + b * a * 0 / 777; // 你好
}

char get_first_ch(char ch[]) {
    return ch[0];
}

int test_stmt() {
    ; // ntsys
    int a= -0;
    {//
      int a = 1;
        
    /**/}
    if (!(a != 0)) {
        int i;
        int j = 0;
        for (i =0 ;i < -1; i = i + 11);
        for (i =1; j<12;) {

            break;
            count = error;
        }   
        for (i= 0;  ;i = i + 1) {
            if (i == 4) {
                break;
            } 
            // printf("i = %d\n", i);
            int j = i - i;
            for (; j != 0; j = j ) {                
                count = error;
            }
            continue;
            count = error - 1;
        }
        printf("i = %d\n", i);

        char name[10] = {'z', 'y', 't'};
        ch1 =get_first_ch(name);
        for (i=1;;){
            printf("heihei, %c\n", ch1);
            if (2 != 1) {break;}
        }
        i = i - i;
        for (;i< -10;) {
            count = error - 11;
        }
        for (;;i = i+1) {
            count = count + 7;
            if (i == 3) {
                break;
            }
        }
        i = 10;
        for(;;){
            int count;
            i = i -1;
            count = i;
            if (i==1) {
                break;
            }
        }
        a = getint();
        char ch = getchar();
        printf("a = %d, ch = %c,sum= %d\n", a, ch, sum(a, ch));
    } else {
        return 'a';
    }
    count += 5;
    return 0;
}

int test_exp(int a[], int num) {
    int ans;
    ans = a[0];
    ans = ans + num * a[1];
    ans = ans + a[2] / num;
    ans = ans + a[3] % num;
    ans = ans + a[4];
    printf("ans1 = %d\n", ans);
    ans = ans - a[5];
    ans = ans + (a[6] > num) * 114;
    ans = ans + (a[7] < num) * 514;
    ans = ans + (a[8] >= num) * 1919;
    ans = ans + (a[9] <= num) * 810;
    ans = ans + (a[10] == num) * 3337;
    ans = ans + (a[11] != num) * 3474;
    printf("ans2 = %d\n", ans);
    ans = ans + ((1==2) && (1==1)) * (-527);
    ans = ans + ((1==2) || (1==1)) * 918;
    ans = ans + ((1==1) && (1==2));
    printf("ans3 = %d\n", ans);
    ans = ans + (1==1) || (1==2) * 1234;
    printf("ans4 = %d\n", ans);
    return -1+-1;
}


int main() {
    hello( );
    test_stmt();
    int a[12] = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
    test_exp(a, 31);
    const int con_num_3 = a[0] + -+1;
    printf("%d,count = %d\n", con_num_3, count);
    return 0;
}