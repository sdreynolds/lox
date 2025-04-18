package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.Assert.assertThat;

class RuntimeErrorsTest {
    @DisplayName("Numbers with String")
    @Test
    void mixingNumbersAndString() {
        final RuntimeError error = assertThrows(
            RuntimeError.class,
            () -> Interpreter.evaluate(
                new Parser(new Scanner("2 * (3 / -\"muffin\")").scanTokens()).parse()),
            "Expected a Runtime Error on the unary negation");
        assertThat(error.getMessage(), containsString("Operand must be a number."));
    }

    @DisplayName("Multiply with String")
    @Test
    void mutiplyingStrings() {
        final RuntimeError error = assertThrows(
            RuntimeError.class,
            () -> Interpreter.evaluate(
                new Parser(new Scanner("2 * \"is this a three and 0\"").scanTokens()).parse()),
            "Expected a Runtime Error when multiplying strings");
        assertThat(error.getMessage(), containsString("Operands must be numbers."));
    }
}
