package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResolverForLoopTest {
    @DisplayName("Counter printer with For Loop")
    @Test
    void forLoopCounterPrinter() throws Exception {
        final var script = "for (var i = 0; i < 2; \ni = i + 1) \nprint i;";
        final var program = new Parser(
            new Scanner("for (var i = 0; i < 2; \ni = i + 1) \nprint i;")
            .scanTokens())
            .parse();
        final var programWithoutNewLines = new Parser(new Scanner(script.replace("\n", "")).scanTokens()).parse();

        assertEquals(program.get(0), programWithoutNewLines.get(0));
    }
}
