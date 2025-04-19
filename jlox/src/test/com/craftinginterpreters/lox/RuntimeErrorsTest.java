package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrNormalized;

class RuntimeErrorsTest {
    @DisplayName("Numbers with String")
    @Test
    void mixingNumbersAndString() {
        final RuntimeError error = assertThrows(
            RuntimeError.class,
            () -> new Interpreter().execute(
                new Parser(new Scanner("2 * (3 / -\"muffin\");").scanTokens()).parse().get(0)),
            "Expected a Runtime Error on the unary negation");
        assertEquals(error.getMessage(), "Operand must be a number.");
    }

    @DisplayName("Multiply with String")
    @Test
    void mutiplyingStrings() {
        final RuntimeError error = assertThrows(
            RuntimeError.class,
            () -> new Interpreter().execute(
                new Parser(new Scanner("2 * \"is this a three and 0\";").scanTokens()).parse().get(0)),
            "Expected a Runtime Error when multiplying strings");
        assertEquals(error.getMessage(), "Operands must be numbers.");
    }

    @DisplayName("Print variable before it is declared")
    @Test
    void printBeforeDeclared() throws Exception {
        final var program = new Parser(new Scanner(
                                           "print a;\nvar a = \"too late!\";"
                                           ).scanTokens()).parse();
        final var output = tapSystemErrNormalized(() -> new Interpreter().interpret(program));
        assertEquals("Undefined var 'a'.\n[line 1]\n", output);
    }
}
