|                 |            |          |           |          |        |          |         |
| :-------------- | :--------- | :------- | :-------- | :------- | :----- | :------- | :------ |
| 单词名称        | 类别码     | 单词名称 | 类别码    | 单词名称 | 类别码 | 单词名称 | 类别码  |
| **Ident**       | IDENFR     | else     | ELSETK    | void     | VOIDTK | ;        | SEMICN  |
| **IntConst**    | INTCON     | !        | NOT       | *        | MULT   | ,        | COMMA   |
| **StringConst** | STRCON     | &&       | AND       | /        | DIV    | (        | LPARENT |
| **CharConst**   | CHRCON     | \|\|     | OR        | %        | MOD    | )        | RPARENT |
| main            | MAINTK     | for      | FORTK     | <        | LSS    | [        | LBRACK  |
| const           | CONSTTK    | getint   | GETINTTK  | <=       | LEQ    | ]        | RBRACK  |
| int             | INTTK      | getchar  | GETCHARTK | >        | GRE    | {        | LBRACE  |
| char            | CHARTK     | printf   | PRINTFTK  | >=       | GEQ    | }        | RBRACE  |
| break           | BREAKTK    | return   | RETURNTK  | ==       | EQL    |          |         |
| continue        | CONTINUETK | +        | PLUS      | !=       | NEQ    |          |         |
| if              | IFTK       | -        | MINU      | =        | ASSIGN |          |         |



![image-20241002145632064](E:\Typora\typora-images\image-20241002145632064.png)

```











乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp 

加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp 

关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp 

相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp 

逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp

逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp

常量表达式 ConstExp → AddExp 注：使用的 Ident 必须是常量 
```