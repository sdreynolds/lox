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
        final var output = tapSystemOutNormalized(() -> Interpreter.execute(print));
        assertEquals("42.33333\n", output);
    }
}
