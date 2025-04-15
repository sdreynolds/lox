package com.craftinginterpreters.lox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.Arguments;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class BasicTokensScanTest {
    @DisplayName("A parameterized test with named argument sets")
    @ParameterizedTest
    @FieldSource("singleTokens")
    public void validTokens(char character, TokenType expectedType) {
        final Scanner scan = new Scanner(Character.toString(character));

        assertIterableEquals(List.of(new Token(expectedType,
                                               Character.toString(character), null, 1),
                             new Token(TokenType.EOF, "", null, 1)),
                             scan.scanTokens());
    }

    static List<Arguments> singleTokens = Arrays.asList(
        arguments('(', TokenType.LEFT_PAREN),
        arguments(')', TokenType.RIGHT_PAREN),
        arguments('{', TokenType.LEFT_BRACE),
        arguments('}', TokenType.RIGHT_BRACE),
        arguments(',', TokenType.COMMA),
        arguments('.', TokenType.DOT),
        arguments('-', TokenType.MINUS),
        arguments('+', TokenType.PLUS),
        arguments(';', TokenType.SEMICOLON),
        arguments('*', TokenType.STAR));

    @DisplayName("Invalid Token Test")
    @Test
    void invalidToken() {
        final Scanner scan = new Scanner("?");
        assertIterableEquals(List.of(new Token(TokenType.EOF, "", null, 1)),
                             scan.scanTokens());
    }
}
