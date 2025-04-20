package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrNormalized;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized;

class FunctionTest {
    @DisplayName("Call time function")
    @Test
    void callTime() throws Exception {
        final var program = new Parser(
            new Scanner(
                "print clock() / 1000;"
                ).scanTokens()
            ).parse();
        final var output = tapSystemErrNormalized(() -> new Interpreter().interpret(program));
        // Should be empty with no error
        assertEquals("", output);
    }

    @DisplayName("Say Hi Function")
    @Test
    void sayHi() throws Exception {
        final var program = new Parser(
            new Scanner(
                "fun sayHi(first, last) { print \"Hi, \" + first + \" \" + last + \"!\";}\n sayHi(\"Dear\", \"Reader\");"
                ).scanTokens()
            ).parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        // Should be empty with no error
        assertEquals("Hi, Dear Reader!\n", output);
    }
}
