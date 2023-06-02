package com.alexGuima.guimaLang;

public class Token {
    final TokenType TYPE;
    final String LEXEME;
    final Object LITERAL;
    final int LINE;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.TYPE = type;
        this.LEXEME = lexeme;
        this.LITERAL = literal;
        this.LINE = line;
    }

    public String toString() {
        return TYPE + " " + LEXEME + " " + LITERAL;
    }
}
