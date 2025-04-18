package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

class InterpreterTest {
    @DisplayName("Simple math")
    @Test
    void simpleMath() {
        final var math = new BinaryExpr(new LiteralExpr(2.0),
                                        new Token(TokenType.PLUS, "+", null, 1),
                                        new LiteralExpr(4.0));
        assertEquals(6.0, Interpreter.evaluate(math));
    }

    @DisplayName("Assert Minus works")
    @Test
    void minusMath() {
        final var math = new BinaryExpr(new LiteralExpr(2.0),
                                        new Token(TokenType.MINUS, "-", null, 1),
                                        new LiteralExpr(4.0));
        assertEquals(-2.0, Interpreter.evaluate(math));
    }

    @DisplayName("Assert Divison Works")
    @Test
    void divsion() {
        final var math = new BinaryExpr(new LiteralExpr(2.0),
                                        new Token(TokenType.SLASH, "/", null, 1),
                                        new LiteralExpr(4.0));
        assertEquals(0.5, Interpreter.evaluate(math));
    }

    @DisplayName("Assert Multiplication Works")
    @Test
    void multi() {
        final var math = new BinaryExpr(new LiteralExpr(2.0),
                                        new Token(TokenType.STAR, "*", null, 1),
                                        new LiteralExpr(4.0));
        assertEquals(8.0, Interpreter.evaluate(math));
    }

    @DisplayName("Concat String together")
    @Test
    void stringConcat() {
        final var strConcat = new BinaryExpr(new BinaryExpr(
                                                 new LiteralExpr("awesome"),
                                                 new Token(TokenType.PLUS, "+", null, 1),
                                                 new LiteralExpr(" ")),
                                             new Token(TokenType.PLUS, "+", null, 1),
                                             new LiteralExpr("sauce")
            );
        assertEquals("awesome sauce", Interpreter.evaluate(strConcat));
    }

    @DisplayName("Greater Than")
    @Test
    void greaterThan() {
        final var compare = new BinaryExpr(
            new LiteralExpr(14.3),
            new Token(TokenType.GREATER, ">", null, 1),
            new LiteralExpr(7.0));
        assertEquals(true, Interpreter.evaluate(compare));
    }

    @DisplayName("Less Than")
    @Test
    void lessThan() {
        final var compare = new BinaryExpr(
            new LiteralExpr(14.3),
            new Token(TokenType.LESS, "<", null, 1),
            new LiteralExpr(7.0));
        assertEquals(false, Interpreter.evaluate(compare));
    }

    @DisplayName("Less Than or Equal")
    @Test
    void lessThanOrEqual() {
        final var compare = new BinaryExpr(
            new LiteralExpr(14.3),
            new Token(TokenType.LESS_EQUAL, "<=", null, 1),
            new LiteralExpr(7.0));
        assertEquals(false, Interpreter.evaluate(compare));
    }

    @DisplayName("Greater Than or Equal")
    @Test
    void greaterThanOrEqual() {
        final var compare = new BinaryExpr(
            new LiteralExpr(14.3),
            new Token(TokenType.GREATER_EQUAL, ">=", null, 1),
            new LiteralExpr(7.0));
        assertEquals(true, Interpreter.evaluate(compare));
    }

    @DisplayName("String is equal")
    @Test
    void equalStrings() {
        final var compare = new BinaryExpr(
            new BinaryExpr(
                new LiteralExpr("awesome "),
                new Token(TokenType.PLUS, "+", null, 1),
                new LiteralExpr("yes")),
            new Token(TokenType.EQUAL_EQUAL, "==", null, 1),
            new LiteralExpr("awesome yes"));
        assertEquals(true, Interpreter.evaluate(compare));
    }

    @DisplayName("String is not equal")
    @Test
    void notEqualStrings() {
        final var compare = new BinaryExpr(
            new BinaryExpr(
                new LiteralExpr("awesome "),
                new Token(TokenType.PLUS, "+", null, 1),
                new LiteralExpr("yes")),
            new Token(TokenType.BANG_EQUAL, "==", null, 1),
            new LiteralExpr("Different"));
        assertEquals(true, Interpreter.evaluate(compare));
    }
}
