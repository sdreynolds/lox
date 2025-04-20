package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.craftinginterpreters.lox.TokenType.*;

class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;
    private int loopCounter = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        final List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            // TODO: check for null and don't add to statement?
            statements.add(declaration());
        }
        return statements;
    }

    private Stmt declaration() {
        try {
            if (match(VAR)) {
                return varDeclaration();
            } else {
                return statement();
            }
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        final var name = consume(IDENTIFIER, "Expect variable name.");

        final Expr initializer;
        if (match(EQUAL)) {
            initializer = expression();
        } else {
            initializer = null;
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new VarStmt(name, initializer);
    }

    private Stmt statement() {
        if (match(PRINT)) {
            return printStatement();
        } else if (match(LEFT_BRACE)) {
            return new BlockStmt(block());
        } else if (match(IF)) {
            return ifStatement();
        } else if (match(WHILE)) {
            loopCounter++;
            try {
                final var stmt =  whileStatement();
                return stmt;
            } finally {
                loopCounter--;
            }
        } else if (match(FOR)) {
            loopCounter++;
            try {
                final var stmt =  forStatement();
                return stmt;
            } finally {
                loopCounter--;
            }
        } else if (match(BREAK)) {
            if (loopCounter == 0) {
                throw error(previous(), "Cannot break from outside of loop.");
            }
            return breakStatement();
        } else if (match(CONTINUE)) {
            if (loopCounter == 0) {
                throw error(previous(), "Cannot continue from outside of a loop.");
            }
            return continueStatement();
        } else {
            return expressionStatement();
        }
    }

    private Stmt continueStatement() {
        // continue;
        consume(SEMICOLON, "Expect ';' after 'continue'.");
        return new ContinueStmt();
    }

    private Stmt breakStatement() {
        // break;
        consume(SEMICOLON, "Expect ';' after 'break'.");
        return new BreakStmt();
    }

    private Stmt forStatement() {
        // for (var i = 0; i < 10; i = i + 1) print i;
        consume(LEFT_PAREN, "Expect '(' after 'for'.");

        final Stmt initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        final Expr condition;
        if (!check(SEMICOLON)) {
            condition = expression();
        } else {
            condition = new LiteralExpr(true);
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");

        final Expr increment;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        } else {
            increment = null;
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");

        Stmt body;

        if (increment != null) {
            body = new BlockStmt(List.of(statement(), new ExpressionStmt(increment)));
        } else {
            body = statement();
        }

        body = new WhileStmt(condition, body);
        if (initializer != null) {
            body = new BlockStmt(List.of(initializer, body));
        }

        return body;
    }

    private Stmt whileStatement() {
        // var a = 0; while (a < 2) {print a; a = a + 1;}
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        final var condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after while condition.");

        final var body = statement();
        return new WhileStmt(condition, body);
    }

    private Stmt ifStatement() {
        // if (a == true) {} else {}
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        final var condition = expression();
        consume (RIGHT_PAREN, "Expect ')' after if condition.");

        final var thenBranch = statement();
        final Optional<Stmt> elseStmt;
        if (match(ELSE)) {
            elseStmt = Optional.ofNullable(statement());
        } else {
            elseStmt = Optional.empty();
        }
        return new IfStmt(condition, thenBranch, elseStmt);
    }

    private List<Stmt> block() {
        final List<Stmt> statements = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd())  {
            statements.add(declaration());
        }
        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private PrintStmt printStatement() {
        final var value = expression();
        consume(SEMICOLON, "Expected ';' after value.");
        return new PrintStmt(value);
    }

    private ExpressionStmt expressionStatement() {
        final var value = expression();
        consume(SEMICOLON, "Expected ';' after expression.");
        return new ExpressionStmt(value);
    }

    private Expr expression () {
        return assignment();
    }

    private Expr assignment() {
        final var expr = or();

        if (match(EQUAL)) {
            final var equals = previous();
            final var value = assignment();

            return switch(expr) {
            case VariableExpr(var name) -> new AssignExpr(name, value);
            default -> {
                // Report an error but return the r-value
                error(equals, "Invalid assignment target.");
                yield expr;
            }
            };
        } else {
            return expr;
        }
    }

    private Expr or() {
        var expr = and();

        while (match(OR)) {
            final var operator = previous();
            final var right = and();
            expr = new LogicalExpr(expr, operator, right);
        }
        return expr;
    }

    private Expr and() {
        var expr = equality();

        while (match(AND)) {
            final var operator = previous();
            final var right = equality();
            expr = new LogicalExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() {
        var expr = comparsion();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            final var operator = previous();
            final var right = comparsion();
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr comparsion() {
        var expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            final var operator = previous();
            final var right = term();
            expr = new BinaryExpr(expr, operator, right);
        }
        return expr;
    }

    private Expr term() {
        var expr = factor();

        while (match(MINUS, PLUS)) {
            final var operator = previous();
            final var right = factor();
            expr = new BinaryExpr(expr, operator, right);
        }
        return expr;
    }

    private Expr factor() {
        var expr = unary();

        while (match(SLASH, STAR)) {
            final var operator = previous();
            final var right = unary();
            expr = new BinaryExpr(expr, operator, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            final var operator = previous();
            final var right = unary();
            return new UnaryExpr(operator, right);
        }
        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) {
            return new LiteralExpr(false);
        }
        if (match(TRUE)) {
            return new LiteralExpr(true);
        }
        if (match(NIL)) {
            return new LiteralExpr(null);
        }

        if (match(NUMBER, STRING)) {
            final var literalExpr = switch(previous()) {
            case Token(var previousType, var _lexeme, var literal, var _line) when previousType == NUMBER -> new LiteralExpr(literal);
            case Token(var _stringType, var _lexeme, var stringLiteral, var _line) -> new LiteralExpr(stringLiteral.toString());
            };
            return literalExpr;
        }

        if (match(LEFT_PAREN)) {
            final var expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new GroupingExpr(expr);
        }

        if (match(IDENTIFIER)) {
            return new VariableExpr(previous());
        }
        throw error(peek(), "Expect expression.");
    }

    private Token consume(final TokenType target, final String errorMessage) {
        if (check(target)) {
            return advance();
        }
        throw error(peek(), errorMessage);
    }

    private ParseError error(final Token token, final String errorMessage) {
        Lox.error(token, errorMessage);
        return new ParseError();
    }

    private void synchronize() {
        advance();
        final var boundaryTypes = Set.of(
            CLASS, FOR, FUN, IF, PRINT, RETURN, VAR, WHILE
        );
        while (!isAtEnd()) {
            if (check(SEMICOLON)) {
                return;
            }

            final boolean canExit = switch(peek()) {
            case Token(var type, var _lexeme, var _literal, var _line) when boundaryTypes.contains(type) -> true;
            default -> false;
            };

            if (canExit) {
                return;
            } else {
                advance();
            }
        }
    }

    private boolean match(TokenType... types) {
        for (var type: types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        } else {
            return switch(peek()) {
            case Token(var peekType, var _lexeme, var _literal, var _line) when type == peekType -> true;
            default -> false;
            };
        }
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return switch(peek()) {
        case Token(var type, var _lexeme, var _literal, var _line) when type == EOF -> true;
        default -> false;
        };
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
