// Global declarations

const int constIntArray[3] = {10, 20, 30};       // Constant integer array
const char constCharArray[5] = {'A', 'B', 'C', 'D', 'E'}; // Constant character array
const char constCharArray2[5] = "abc"; // Constant character array
int intArray[5];                                 // Integer array
char charArray[5];                               // Character array

int func_with_param(int a, char b, int arr[], char str[]) {
    printf("Function with parameters: a = %d, b = %c arr[0] = %d, str[0] = %c\n", a, b, arr[0], str[0]);
    int sum = a + b + arr[0] + str[0];
    printf("Sum in func_with_param: %d\n", sum);
    return sum;
}

int main() {
    printf("22373141\n"); // 1st printf (Replace with your actual student ID)

    // Initialize intArray using constIntArray
    intArray[0] = constIntArray[0];
    intArray[1] = constIntArray[1];
    intArray[2] = constIntArray[2];
    intArray[3] = intArray[0] + intArray[1];
    intArray[4] = intArray[3] + intArray[2];

    // Using unary '-' operator
    intArray[0] = -intArray[0];
    printf("Negative intArray[0]: %d\n", intArray[0]); // 2nd printf

    // Using unary '+' operator
    intArray[0] = +intArray[0];
    printf("Positive intArray[0]: %d\n", intArray[0]); // 3rd printf

    // Using '/' and '%'
    intArray[1] = intArray[3] / intArray[2];
    intArray[2] = intArray[3] % intArray[2];
    printf("Quotient: %d, Remainder: %d\n", intArray[1], intArray[2]); // 4th printf


    // Copy constCharArray to charArray and modify to sum over 128
    charArray[0] = constCharArray[0] + constCharArray[1] + constCharArray[2] + constCharArray[3] + constCharArray[4];

    printf("Sum of ASCII codes1: %d %c\n", charArray[0], charArray[0]);

    int result = func_with_param(intArray[0], charArray[0], intArray, charArray);

    // Calculate sum of ASCII codes
    int asciiSum = constCharArray2[0] + constCharArray2[1] + constCharArray2[2] + constCharArray2[3] + constCharArray2[4];
    char charSum = constCharArray2[0] + constCharArray2[1] + constCharArray2[2] + constCharArray2[3] + constCharArray2[4];

    printf("Sum of ASCII codes2: %d %c\n", asciiSum, charSum);


    // Example 1
    int x1 = 'a' + 10; // 'a' is ASCII 97, so x1 = 97 + 10 = 107
    printf("x1 = %d\n", x1); // 7th printf

    // Example 2
    char a1 = '0' - 7; // '0' is ASCII 48, so a1 = 48 - 7 = 41 (')')
    printf("a1 = %d, as char: %c\n", a1, a1); // 8th printf

    return 0;
}

