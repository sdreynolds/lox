package com.craftinginterpreters.lox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.Arguments;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class BasicTokensScanTest {
    @DisplayName("Invalid Token Test")
    @Test
    void invalidToken() {
        final Scanner scan = new Scanner("?");
        assertIterableEquals(List.of(new Token(TokenType.EOF, "", null, 1)),
                             scan.scanTokens());
    }

    @DisplayName("Test all the single tokens from a single line")
    @ParameterizedTest
    @FieldSource("singleLineSingleTokens")
    void singleLineTokens(final String line, final TokenType expectedType) {
        final Scanner scan = new Scanner(line);
        // We know this is fine for this case.
        final int newLineCount = (int)line.chars().filter(ch -> ch == '\n').count();
        assertIterableEquals(
            List.of(new Token(expectedType, line.replaceAll("\\n", "").replace(" ", "").replace("\r", "").replace("\t", ""), null, 1),
                    new Token(TokenType.EOF, "", null, newLineCount + 1)),
            scan.scanTokens());
    }

    static List<Arguments> singleLineSingleTokens = Arrays.asList(
        arguments("!=", TokenType.BANG_EQUAL),
        arguments("!", TokenType.BANG),
        arguments("=", TokenType.EQUAL),
        arguments("==", TokenType.EQUAL_EQUAL),
        arguments("<=", TokenType.LESS_EQUAL),
        arguments("<", TokenType.LESS),
        arguments(">", TokenType.GREATER),
        arguments(">=", TokenType.GREATER_EQUAL),

        arguments("(", TokenType.LEFT_PAREN),
        arguments(")", TokenType.RIGHT_PAREN),
        arguments("{", TokenType.LEFT_BRACE),
        arguments("}", TokenType.RIGHT_BRACE),
        arguments(",", TokenType.COMMA),
        arguments(".", TokenType.DOT),
        arguments("-", TokenType.MINUS),
        arguments("+", TokenType.PLUS),
        arguments(";", TokenType.SEMICOLON),
        arguments("*", TokenType.STAR),
        arguments("/", TokenType.SLASH),

        arguments("     \t\r        (", TokenType.LEFT_PAREN),

        // Keywords
        arguments("and", TokenType.AND),
        arguments("class", TokenType.CLASS),
        arguments("else", TokenType.ELSE),
        arguments("false", TokenType.FALSE),
        arguments("for", TokenType.FOR),
        arguments("fun", TokenType.FUN),
        arguments("if", TokenType.IF),
        arguments("nil", TokenType.NIL),
        arguments("or", TokenType.OR),
        arguments("print", TokenType.PRINT),
        arguments("return", TokenType.RETURN),
        arguments("super", TokenType.SUPER),
        arguments("this", TokenType.THIS),
        arguments("true", TokenType.TRUE),
        arguments("var", TokenType.VAR),
        arguments("while", TokenType.WHILE)
        );

    @DisplayName("Test string literal parsing")
    @Test
    void stringLiteral() {
        final Scanner scan = new Scanner("\"This is a test string\"");
        assertIterableEquals(List.of(
                                 new Token(TokenType.STRING, "\"This is a test string\"", "This is a test string" , 1),
                                 new Token(TokenType.EOF, "", null, 1)),
            scan.scanTokens());
    }

    @DisplayName("Testing number literals")
    @ParameterizedTest
    @ValueSource(strings = {"12345", "12.34", "2.356", "1223500606.38"})
    void numberLineTokens(final String line) {
        final Scanner scan = new Scanner(line);
        double expectedValue = Double.parseDouble(line);

        assertIterableEquals(List.of(
                                 new Token(TokenType.NUMBER, line, expectedValue, 1),
                                 new Token(TokenType.EOF, "", null, 1)),
                             scan.scanTokens());
    }

    @DisplayName("Parsing identifier orchid")
    @Test
    void parseIdentifierOrchid() {
        final Scanner scan = new Scanner("orchid");
        assertIterableEquals(List.of(new Token(TokenType.IDENTIFIER, "orchid", null, 1),
                new Token(TokenType.EOF, "", null, 1)), scan.scanTokens());
    }

    @DisplayName("Multiline Comment Test")
    @Test
    void multilinecomment() {
        final Scanner scan = new Scanner("/* \n multi line \ncomment*/ 2.3456");
        assertIterableEquals(List.of(new Token(TokenType.NUMBER, "2.3456", 2.3456d, 3),
                new Token(TokenType.EOF, "", null, 3)), scan.scanTokens());
    }

    @DisplayName("Open ended Comment Test")
    @Test
    void nonTerminatedComment() {
        final Scanner scan = new Scanner("/* \n multi line \ncomment 2.3456");
        assertIterableEquals(List.of(new Token(TokenType.EOF, "", null, 3)), scan.scanTokens());
    }

    @DisplayName("Open ended Comment Test missing closing slash")
    @Test
    void almostTerminatedCommnet() {
        final Scanner scan = new Scanner("/* \n multi line \ncomment* 2.3456");
        assertIterableEquals(List.of(new Token(TokenType.EOF, "", null, 3)), scan.scanTokens());
    }
}
