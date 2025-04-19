package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized;

class PrintStmtTest {

    @DisplayName("Basic Print command")
    @Test
    void basicPrintStmt() throws Exception {
        final var print = new PrintStmt(new LiteralExpr(42.33333));
        final var output = tapSystemOutNormalized(() -> new Interpreter().execute(print));
        assertEquals("42.33333\n", output);
    }

    @DisplayName("Full Print end to end")
    @Test
    void endToEnd() throws Exception {
        final var print = new Parser(
            new Scanner("print \"one\";\nprint true;\nprint 2 +      1;").scanTokens()).parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(print));
        assertEquals("one\ntrue\n3\n", output);
    }
}
