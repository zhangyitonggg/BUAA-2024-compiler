int a = 1;
int func() {
    a = 2;
    return 1;
}

int func2() {
    a = 4;
    return 10;
}
int func3() {
    a = 3;
    return 0;
}
int main() {
    if (0 || func() && func3() || func2()) {
        printf("%d--1", a);
    }
    if (1 || func3()) {
        printf("%d--2", a);
    }
    if (0 || func3() || func() < func2()) {
        printf("%d--3", a);
    }
    return 0;
}