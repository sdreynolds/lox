package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

class BasicParserTest {
    @DisplayName("Basic addition")
    @Test
    void addition() {
        final List<Token> tokens = List.of(
            new Token(TokenType.NUMBER, "2", 2, 1),
            new Token(TokenType.PLUS, "+", null, 1),
            new Token(TokenType.NUMBER, "4", 4, 1),
            new Token(TokenType.EOF, "", null, 1)
        );
        final var parser = new Parser(tokens);
        assertEquals(
            "(+ 2 4)",
            AstPrinter.print(parser.parse())
        );
    }
}
