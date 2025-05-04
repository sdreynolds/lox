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
        assertEquals("Hi, Dear Reader!\n", output);
    }

    @DisplayName("Nested return from calls")
    @Test
    void nestedReturn() throws Exception {
        final var program = new Parser(
            new Scanner(
                "fun count(n) { while (n < 100) { if (n == 3) return n; print n; n = n + 1;}} count(1);"
                ).scanTokens()
            ).parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("1\n2\n", output);
    }

    @DisplayName("Finonacci Test")
    @Test
    void fib() throws Exception {
        final var program = new Parser(
            new Scanner(
                "fun fib(n) { if (n <= 1) return n; return fib(n - 2) + fib (n - 1);} print fib(18);"
                ).scanTokens()
            ).parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("2584\n", output);
    }

    @DisplayName("counter scope test")
    @Test
    void counterTest() throws Exception {
        final var program = new Parser(
            new Scanner(
                "fun makeCounter() { var i = 0; fun count() { i = i + 1; print i;} return count;}  var counter = makeCounter(); counter(); counter();")
            .scanTokens())
            .parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("1\n2\n", output);
    }
}
