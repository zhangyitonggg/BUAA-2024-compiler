package frontend.lexer;

public enum TokenType {
    IDENFR,     // Ident            **
    INTCON,     // IntConst         **
    STRCON,     // FormatString     **
    CHRCON,     // CharConst        **
    MAINTK,     // main
    CONSTTK,    // const
    INTTK,      // int
    CHARTK,     // char
    BREAKTK,    // break
    CONTINUETK, // continue
    IFTK,       // if
    ELSETK,     // else
    NOT,        // !
    AND,        // &&
    OR,         // ||
    FORTK,      // for
    GETINTTK,   // getint
    GETCHARTK,  // getchar
    PRINTFTK,   // printf
    RETURNTK,   // return
    PLUS,       // +
    MINU,       // -
    VOIDTK,     // void
    MULT,       // *
    DIV,        // /
    MOD,        // %
    LSS,        // <
    LEQ,        // <=
    GRE,        // >
    GEQ,        // >=
    EQL,        // ==
    NEQ,        // !=
    ASSIGN,     // =
    SEMICN,     // ;
    COMMA,      // ,
    LPARENT,    // (
    RPARENT,    // )
    LBRACK,     // [
    RBRACK,     // ]
    LBRACE,     // {
    RBRACE,     // }
    EOF,        // end
}
