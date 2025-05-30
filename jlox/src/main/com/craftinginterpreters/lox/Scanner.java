package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords = Map.ofEntries(
        Map.entry("and", AND),
        Map.entry("class", CLASS),
        Map.entry("else", ELSE),
        Map.entry("false", FALSE),
        Map.entry("for", FOR),
        Map.entry("fun", FUN),
        Map.entry("if", IF),
        Map.entry("nil", NIL),
        Map.entry("or", OR),
        Map.entry("print", PRINT),
        Map.entry("return", RETURN),
        Map.entry("super", SUPER),
        Map.entry("this", THIS),
        Map.entry("true", TRUE),
        Map.entry("var", VAR),
        Map.entry("while", WHILE),
        Map.entry("break", BREAK),
        Map.entry("continue", CONTINUE)
        );

    Scanner(String source) {
        this.source = source;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
        case '(': addToken(LEFT_PAREN); break;
        case ')': addToken(RIGHT_PAREN); break;
        case '{': addToken(LEFT_BRACE); break;
        case '}': addToken(RIGHT_BRACE); break;
        case ',': addToken(COMMA); break;
        case '.': addToken(DOT); break;
        case '-': addToken(MINUS); break;
        case '+': addToken(PLUS); break;
        case ';': addToken(SEMICOLON); break;
        case '*': addToken(STAR); break;
        case '!':
            addToken(match('=') ? BANG_EQUAL : BANG);
            break ;
        case '=':
            addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            break;
        case '<':
            addToken(match('=') ? LESS_EQUAL : LESS);
            break;
        case '>':
            addToken(match('=') ? GREATER_EQUAL : GREATER);
            break;

        case '/':
            if (match('/')) {
                // A comment! go to end of line
                while (peek() != '\n' && !isAtEnd()) {
                    advance();
                }
            } else if (match('*')) {
                while ((peek() != '*' || peekNext() != '/') && !isAtEnd()) {
                    if (peek() == '\n') {
                        line++;
                    }
                    advance();
                }

                if (!isAtEnd()) {
                    advance();
                    advance();
                }
            } else {
                addToken(SLASH);
            }
            break;

        case ' ', '\r', '\t': break;
        case '\n':
            line++;
            break;

        case '"':
            string();
            break;


        default:
            if (isDigit(c)) {
                number();
            } else if (isAlpha(c)) {
                identifier();
            } else {
                Lox.error(line, "Unexpected character.");
            }
            break;
        }
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
            (c >= 'A' && c <= 'Z') ||
            (c == '_');
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }

        final String text = source.substring(start, current);
        addToken(keywords.getOrDefault(text, IDENTIFIER));
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.' && isDigit(peekNext())) {
            advance();
        }

        while (isDigit(peek())) {
            advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();

        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
        }

        advance();

        final String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        } else {
            return source.charAt(current + 1);
        }
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        } else if (source.charAt(current) != expected) {
            return false;
        } else {
            current++;
            return true;
        }
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }
}
