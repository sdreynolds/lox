package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.craftinginterpreters.lox.TokenType.*;

class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

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
        } else {
            return expressionStatement();
        }
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
        final var expr = equality();

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
