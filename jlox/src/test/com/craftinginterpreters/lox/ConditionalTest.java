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

    @DisplayName("Logical OR conditioning with truthy first case")
    @Test
    void truthyFirstCase() throws Exception {
        final var program = new Parser(
            new Scanner("print \"hi\" or 2;")
            .scanTokens())
            .parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("hi\n", output);
    }

    @DisplayName("Logical OR conditioning with truthy second case")
    @Test
    void truthySecondCase() throws Exception {
        final var program = new Parser(
            new Scanner("var a; print a or nil or 2;")
            .scanTokens())
            .parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("2\n", output);
    }

    @DisplayName("Multiple AND conditionals")
    @Test
    void multipleAnd() throws Exception {
        final var program = new Parser(
            new Scanner("var a = \"awesome\"; if (a and true and \"yo\" and \"sup\") print a;")
            .scanTokens())
            .parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("awesome\n", output);
    }

    @DisplayName("Counter printer")
    @Test
    void counterPrint() throws Exception {
        final var program = new Parser(
            new Scanner("var a = 0; while (a < 2) {print a; a = a + 1;}")
            .scanTokens())
            .parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("0\n1\n", output);
    }

    @DisplayName("Counter printer with For Loop")
    @Test
    void forLoopCounterPrinter() throws Exception {
        final var program = new Parser(
            new Scanner("for (var i = 0; i < 2; i = i + 1) print i;")
            .scanTokens())
            .parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("0\n1\n", output);
    }

    @DisplayName("Counter printer with For Loop without initializer")
    @Test
    void forLoopCounterPrinterNoInit() throws Exception {
        final var program = new Parser(
            new Scanner("var i = 0; for (; i < 2; i = i + 1) print i;")
            .scanTokens())
            .parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("0\n1\n", output);
    }

    @DisplayName("Counter printer with For Loop without increment")
    @Test
    void forLoopCounterPrinterNoIncr() throws Exception {
        final var program = new Parser(
            new Scanner("var i; for (i = 0; i < 2; ) {print i; i = i + 1;}")
            .scanTokens())
            .parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("0\n1\n", output);
    }

    @DisplayName("Break out of loop")
    @Test
    void breakingOut() throws Exception {
        final var program = new Parser(
            new Scanner("var i; for (i = 0; i < 10; ) {print i; i = i + 1; if (i >= 2) break; print \"yep\";} print \"exit\";")
            .scanTokens())
            .parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("0\nyep\n1\nexit\n", output);
    }

    @DisplayName("Nested For Loops with Break")
    @Test
    void nestedForLoopsBreak() throws Exception {
        final var parser = new Parser(
            new Scanner("for (var i = 0; i < 10; i = i + 1) {\n for (var j = 0; j < 10; j = j + 1)\n { print j; break;}}")
            .scanTokens());
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(parser.parse()));
        assertEquals("0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n", output);
    }

    @DisplayName("Continue Loop")
    @Test
    void continueLoop() throws Exception {
        // The following program creates an infinite loop because the increment is ignored after
        // i > 5
        // for (var i = 0; i < 10; i = i + 1) {if (i > 5) continue; print i;}
        final var program = new Parser(
            new Scanner(
                "var i = 0; while(i < 10) {i = i + 1; if (i > 5) continue; print i;} print \"exit\";"
                ).scanTokens())
            .parse();
        final var output = tapSystemOutNormalized(() -> new Interpreter().interpret(program));
        assertEquals("1\n2\n3\n4\n5\nexit\n", output);
    }
}
