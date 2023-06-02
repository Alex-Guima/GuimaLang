package com.alexGuima.guimaLang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alexGuima.guimaLang.TokenType.*;

class Scanner {
    private final String SOURCE;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.SOURCE = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch(c) {
            case '(': addToken(L_PAREN); break;
            case ')': addToken(R_PAREN); break;
            case '{': addToken(L_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken((match('=') ? EQUAL_EQUAL : EQUAL));
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    while(peek() != '\n' &&!isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n':
                line++;
                break;

            case '"': string(); break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    GuimaLang.error(line, "Unexpected character.");
                }
                break;

        }
    }

    private  static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",      AND);
        keywords.put("class",    CLASS);
        keywords.put("else",     ELSE);
        keywords.put("false",    FALSE);
        keywords.put("for",      FOR);
        keywords.put("function", FUNCTION);
        keywords.put("if",       IF);
        keywords.put("nil",      NIL);
        keywords.put("or",       OR);
        keywords.put("print",    PRINT);
        keywords.put("return",   RETURN);
        keywords.put("super",    SUPER);
        keywords.put("this",     THIS);
        keywords.put("true",     TRUE);
        keywords.put("var",      VAR);
        keywords.put("while",    WHILE);
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = SOURCE.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) advance();

        if (peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER,
                Double.parseDouble(SOURCE.substring(start, current)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            GuimaLang.error(line, "Unterminated string.");
            return;
        }

        advance();

        String value = SOURCE.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (SOURCE.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return SOURCE.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= SOURCE.length()) return '\0';
        return SOURCE.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <='9';
    }

    private boolean isAtEnd() {
        return current >= SOURCE.length();
    }

    private char advance() {
        return SOURCE.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = SOURCE.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
