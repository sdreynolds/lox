package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrNormalized;

class ResolverEnd2EndTest {
    @DisplayName("Scope test for printing")
    @Test
    void globalAndBlockScope() throws Exception {
        final var program = new Parser(
            new Scanner(
                "var a = \"global\"; { fun showA() { print a;} showA(); var a = \"block\"; showA();}")
            .scanTokens())
            .parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("global\nglobal\n", output);
    }

    @DisplayName("Initializer referencing itself")
    @Test
    void initSelfReference() throws Exception {
        final var program = new Parser(
            new Scanner(
                "var a = \"outer\"; { var a = a; } ")
            .scanTokens())
            .parse();
        final var output = tapSystemErrNormalized(() -> new Interpreter().interpret(program));
        assertEquals("global\nglobal\n", output);
    }
}
