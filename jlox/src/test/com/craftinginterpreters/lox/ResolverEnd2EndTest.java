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
                "var a = \"global\"; \n{ \nfun showA() \n{ \nprint a;\n} \nshowA(); \nvar a = \"block\"; \nshowA();\n}")
            .scanTokens())
            .parse();
        final var interpreter = new Interpreter();
        final var resolver = new Resolver(interpreter);
        resolver.resolve(program);
        final var output = tapSystemOutNormalized(() -> interpreter.interpret(program));
        assertEquals("global\nglobal\n", output);
    }

    @DisplayName("Initializer referencing itself")
    @Test
    void initSelfReference() throws Exception {
        final var program = new Parser(
            new Scanner(
                "var a = \"outer\"; \n{ \nvar a = a; \n} ")
            .scanTokens())
            .parse();
        final var interpreter = new Interpreter();
        final var resolver = new Resolver(interpreter);
        final var output = tapSystemErrNormalized(() -> {
                resolver.resolve(program);
                interpreter.interpret(program);
            });
        assertEquals("[line 3] Error at 'a': Cannot read local variable in its own initializer.\n", output);
    }
}
