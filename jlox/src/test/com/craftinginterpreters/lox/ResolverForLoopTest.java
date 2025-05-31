package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrNormalized;

class ResolverForLoopTest {
    @DisplayName("Counter printer with For Loop")
    @Test
    void forLoopCounterPrinter() throws Exception {
        final var script = "for (var i = 0; i < 2; \ni = i + 1) \nprint i;";
        final var program = new Parser(
            new Scanner(script)
            .scanTokens())
            .parse();

        final var interpreter = new Interpreter();
        new Resolver(interpreter).resolve(program);

        assertEquals("", tapSystemErrNormalized(() -> interpreter.interpret(program)),
                     "Program with new lines should execute successfully");
    }
}
