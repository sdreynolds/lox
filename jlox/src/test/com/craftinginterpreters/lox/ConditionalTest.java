package com.craftinginterpreters.lox;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized;

class ConditionalTest {
    @DisplayName("If condition without Else is parsed")
    @Test
    void parseIfStmt() {
        final var program = new Parser(
            new Scanner("if (23 > (-12 + 10)) print \"success\";")
            .scanTokens())
          .parse();
        assertEquals(
                new IfStmt(new BinaryExpr(
                               new LiteralExpr(23.0),
                               new Token(TokenType.GREATER, ">", null, 1),
                               new GroupingExpr(
                                   new BinaryExpr(
                                       new UnaryExpr(new Token(TokenType.MINUS, "-", null, 1), new LiteralExpr(12.0)),
                                       new Token(TokenType.PLUS, "+", null, 1),
                                       new LiteralExpr(10.0)))
                               ),
                           new PrintStmt(new LiteralExpr("success")),
                           Optional.empty()),
                program.get(0));
    }

    @DisplayName("Process thenBranch")
    @Test
    void thenBranch() throws Exception {
        final var stmt = new IfStmt(
            new LiteralExpr(Boolean.TRUE),
            new PrintStmt(new LiteralExpr(14)),
            Optional.empty());
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(List.of(stmt)));
        assertEquals("14\n", output);
    }

    @DisplayName("Execute elseBranch")
    @Test
    void elseBranch() throws Exception {
        final var stmt = new IfStmt(
            new LiteralExpr(Boolean.FALSE),
            new PrintStmt(new LiteralExpr(14)),
            Optional.of(new PrintStmt(new LiteralExpr(32))));
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(List.of(stmt)));
        assertEquals("32\n", output);
    }

    @DisplayName("Empty Else Branch")
    @Test
    void emptyElseBranch() throws Exception {
        final var stmt = new IfStmt(
            new LiteralExpr(Boolean.FALSE),
            new PrintStmt(new LiteralExpr(14)),
            Optional.empty());
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(List.of(stmt)));
        assertEquals("", output);
    }
}
