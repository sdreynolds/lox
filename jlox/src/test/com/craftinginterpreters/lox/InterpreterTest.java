package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrNormalized;

class InterpreterTest {
    @DisplayName("Simple math")
    @Test
    void simpleMath() {
        final var math = new BinaryExpr(new LiteralExpr(2.0),
                                        new Token(TokenType.PLUS, "+", null, 1),
                                        new LiteralExpr(4.0));
        assertEquals(6.0, new Interpreter().evaluate(math));
    }

    @DisplayName("Assert Minus works")
    @Test
    void minusMath() {
        final var math = new BinaryExpr(new LiteralExpr(2.0),
                                        new Token(TokenType.MINUS, "-", null, 1),
                                        new LiteralExpr(4.0));
        assertEquals(-2.0, new Interpreter().evaluate(math));
    }

    @DisplayName("Assert Divison Works")
    @Test
    void divsion() {
        final var math = new BinaryExpr(new LiteralExpr(2.0),
                                        new Token(TokenType.SLASH, "/", null, 1),
                                        new LiteralExpr(4.0));
        assertEquals(0.5, new Interpreter().evaluate(math));
    }

    @DisplayName("Assert Multiplication Works")
    @Test
    void multi() {
        final var math = new BinaryExpr(new LiteralExpr(2.0),
                                        new Token(TokenType.STAR, "*", null, 1),
                                        new LiteralExpr(4.0));
        assertEquals(8.0, new Interpreter().evaluate(math));
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
        assertEquals("awesome sauce", new Interpreter().evaluate(strConcat));
    }

    @DisplayName("Greater Than")
    @Test
    void greaterThan() {
        final var compare = new BinaryExpr(
            new LiteralExpr(14.3),
            new Token(TokenType.GREATER, ">", null, 1),
            new LiteralExpr(7.0));
        assertEquals(true, new Interpreter().evaluate(compare));
    }

    @DisplayName("Less Than")
    @Test
    void lessThan() {
        final var compare = new BinaryExpr(
            new LiteralExpr(14.3),
            new Token(TokenType.LESS, "<", null, 1),
            new LiteralExpr(7.0));
        assertEquals(false, new Interpreter().evaluate(compare));
    }

    @DisplayName("Less Than or Equal")
    @Test
    void lessThanOrEqual() {
        final var compare = new BinaryExpr(
            new LiteralExpr(14.3),
            new Token(TokenType.LESS_EQUAL, "<=", null, 1),
            new LiteralExpr(7.0));
        assertEquals(false, new Interpreter().evaluate(compare));
    }

    @DisplayName("Greater Than or Equal")
    @Test
    void greaterThanOrEqual() {
        final var compare = new BinaryExpr(
            new LiteralExpr(14.3),
            new Token(TokenType.GREATER_EQUAL, ">=", null, 1),
            new LiteralExpr(7.0));
        assertEquals(true, new Interpreter().evaluate(compare));
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
        assertEquals(true, new Interpreter().evaluate(compare));
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
        assertEquals(true, new Interpreter().evaluate(compare));
    }

    @DisplayName("Undeclared Assignment")
    @Test
    void undeclaredAssignment() throws Exception {
        final var program = List.<Stmt>of(
            new PrintStmt(new AssignExpr(
                              new Token(TokenType.IDENTIFIER, "a", null, 1),
                              new LiteralExpr("reassigned")))
            );

        final var output = tapSystemErrNormalized(() -> new Interpreter().interpret(program));
        assertEquals("Undefined variable 'a'.\n[line 1]\n", output);
    }

    @DisplayName("Reassignment")
    @Test
    void reassignment() throws Exception {
        final var program = List.<Stmt>of(
            new VarStmt(new Token(TokenType.IDENTIFIER, "a", null, 1),
                        new LiteralExpr("before")),
            new PrintStmt(new AssignExpr(
                              new Token(TokenType.IDENTIFIER, "a", null, 1),
                              new LiteralExpr("reassigned")))
        );

        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("reassigned\n", output);
    }
}
