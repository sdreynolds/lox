package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class AstPrinterTest {
    @DisplayName("Test on numeric printing")
    @ParameterizedTest
    @ValueSource(doubles = {1.0, 2.4, 45.6878696})
    void printNumbers(double v) {
        assertEquals(
            Double.toString(v),
            AstPrinter.print(new LiteralExpr(v)));
    }

    @DisplayName("Print Bang negation")
    @Test
    void printBangNegation() {
        assertEquals(
            "(! true)",
            AstPrinter.print(new UnaryExpr(
                                 new Token(TokenType.BANG, "!", null, 1),
                                 new LiteralExpr("true"))));
    }

    @DisplayName("Print addition Expr")
    @Test
    void printAddition() {
        assertEquals(
            "(+ 2.0 3)",
            AstPrinter.print(new BinaryExpr(
                                 new LiteralExpr(2.0),
                                 new Token(TokenType.PLUS, "+", null, 1),
                                 new LiteralExpr(3)))
        );
    }

    @DisplayName("Example tree from page 74")
    @Test
    void exampleTree() {
        assertEquals(
            "(* (- 123) (group 45.67))",
            AstPrinter.print(new BinaryExpr(
                                 new UnaryExpr(new Token(TokenType.MINUS, "-", null, 1), new LiteralExpr(123)),
                                 new Token(TokenType.STAR, "*", null, 1),
                                 new GroupingExpr(new LiteralExpr(45.67d))))
        );
    }
}
