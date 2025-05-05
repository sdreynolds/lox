package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrNormalized;

class BlockEndToEndTest {
    @DisplayName("Testing Scope")
    @Test
    void scopeTesting() throws Exception {
        final var program = new Parser(
            new Scanner(
                "var c = \"awesome\"; var a = \"global a\";\nvar b = \"global b\"; {a = \"inner a\"; var b = \"inner b\"; print a; print b; print c;} print a; print b;"
                ).scanTokens()).parse();
        final var output = tapSystemOutNormalized(() -> {
                final var interpreter = new Interpreter();
                final var resolver = new Resolver(interpreter);
                resolver.resolve(program);
                interpreter.interpret(program);
            });
        assertEquals("inner a\ninner b\nawesome\ninner a\nglobal b\n", output);
    }

    @DisplayName("Challenge program")
    @Test
    void challengeProgram() throws Exception {
        final var program = new Parser(
            new Scanner(
                "var a = 1; { var a = a + 2; print a;} print a;"
                ).scanTokens()).parse();
        final var output = tapSystemErrNormalized(() -> {
                final var interpreter = new Interpreter();
                final var resolver = new Resolver(interpreter);
                resolver.resolve(program);
                interpreter.interpret(program);
            });
        assertEquals("[line 1] Error at 'a': Cannot read local variable in its own initializer.\nOperands must be two numbers or two strings.\n[line 1]\n", output);
    }
}
