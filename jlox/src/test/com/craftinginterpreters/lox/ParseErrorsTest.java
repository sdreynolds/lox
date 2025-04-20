package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrNormalized;

class ParseErrorsTest {
    @DisplayName("Bad assignment")
    @Test
    void incorrectLValue() throws Exception {
        final var parser = new Parser(new Scanner(
                                           "var a = 2; var b = 3; a + b = c;"
                                          ).scanTokens());
        final var output = tapSystemErrNormalized(() -> parser.parse());
        assertEquals("[line 1] Error at '=': Invalid assignment target.\n", output);
    }

    @DisplayName("Parentheses assignment")
    @Test
    void parenthesesAssignment() throws Exception {
        final var parser = new Parser(new Scanner(
                                          "(a) = 3;"
                                          ).scanTokens());
        final var output = tapSystemErrNormalized(() -> parser.parse());
        assertEquals("[line 1] Error at '=': Invalid assignment target.\n", output);
    }

    @DisplayName("Missing Semicolon")
    @Test
    void missingSemiColon() throws Exception {
        final var parser = new Parser(new Scanner("var a = 3").scanTokens());
        final var output = tapSystemErrNormalized(() -> parser.parse());
        assertEquals("[line 1] Error at end: Expect ';' after variable declaration.\n", output);
    }

    @DisplayName("Break outside of loop")
    @Test
    void breakOutsideOfLoop() throws Exception {
        final var parser = new Parser(
            new Scanner("var i = 0;print i; i = i + 1; if (i >= 2) break;")
            .scanTokens());
        final var output = tapSystemErrNormalized(() -> parser.parse());
        assertEquals("[line 1] Error at 'break': Cannot break from outside of loop.\n", output);
    }

    @DisplayName("Continue outside of loop")
    @Test
    void continueOutsideOfLoop() throws Exception {
        final var parser = new Parser(
            new Scanner("var i = 0;print i; i = i + 1; if (i >= 2) continue;")
            .scanTokens());
        final var output = tapSystemErrNormalized(() -> parser.parse());
        assertEquals("[line 1] Error at 'continue': Cannot continue from outside of a loop.\n", output);
    }
}
