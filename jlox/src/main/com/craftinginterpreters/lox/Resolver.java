package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Resolver {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    void resolve(final List<Stmt> statements) {
        for (var statement: statements) {
            resolveStatement(statement);
        }
    }

    private void resolveStatement(final Stmt statement) {
        switch(statement) {
        case BlockStmt(var blockStatements) -> {
            beginScope();
            resolve(blockStatements);
            endScope();
        }
        case FunctionStmt(Token(var _type, var functionName, var _literal, var _line), var params, var body) -> {
            declare(functionName);
            define(functionName);
            beginScope();
            params.stream().map(token -> token.lexeme()).forEach(name -> {
                    declare(name);
                    define(name);
            });
            resolve(body);
            endScope();
        }
        case VarStmt(Token(var _type, var varName, var _literal, var _line), var varInit) -> {
            declare(varName);
            System.out.println("Declaring " + varName);
            if (varInit != null) {
                resolveExpression(varInit);
            }
            define(varName);
            System.out.println("Defined " + varName);
        }
        case ExpressionStmt(var expression) -> resolveExpression(expression);
        case IfStmt(var condition, var thenBranch, var elseBranch) when elseBranch.isPresent() -> {
            resolveExpression(condition);
            resolveStatement(thenBranch);
            resolveStatement(elseBranch.get());
        }
        case IfStmt(var condition, var thenBranch, var _elseBranch) -> {
            resolveExpression(condition);
            resolveStatement(thenBranch);
        }
        case PrintStmt(var printExpression) -> resolveExpression(printExpression);
        case ReturnStmt(var _keyword, var value) -> value.ifPresent(v -> resolveExpression(v));
        case WhileStmt(var whileCondition, var whileBody) -> {
            resolveExpression(whileCondition);
            resolveStatement(whileBody);
        }
        case ContinueStmt() -> ensureInLoop();
        case BreakStmt() -> ensureInLoop();

        }
    }

    private void ensureInLoop() {
        // @TODO: ensure we have visited a While Statement and still in that scope.
        return;
    }

    private void resolveExpression(final Expr expression) {
        switch(expression) {
        case VariableExpr(var variableToken) -> {
            if (!scopes.isEmpty() && scopes.peek().get(variableToken.lexeme()) == Boolean.FALSE) {
                Lox.error(variableToken, "Cannot read local variable in its own initializer.");
            }
            resolveLocal(expression, variableToken.lexeme());
        }
        case AssignExpr(Token(var _type, var assignName, var _literal, var _line), var assignValue) -> {
            resolveExpression(assignValue);
            resolveLocal(expression, assignName);
        }
        case BinaryExpr(var left, var _operator, var right) -> {
            resolveExpression(left);
            resolveExpression(right);
        }
        case CallExpr(var callee, var _paren, var callArguments) -> {
            resolveExpression(callee);
            callArguments.stream().forEach(a -> resolveExpression(a));
        }
        case GroupingExpr(var groupExpression) -> resolveExpression(groupExpression);
        case LiteralExpr(var _objValue) -> {break;}
        case LogicalExpr(var logicalLeft, var _operator, var logicalRight) -> {
            resolveExpression(logicalLeft);
            resolveExpression(logicalRight);
        }
        case UnaryExpr(var _unaryOp, var unaryTarget) -> resolveExpression(unaryTarget);
        }
    }

    private void resolveLocal(Expr expression, String name) {
        for (var i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                interpreter.resolve(expression, scopes.size() - 1 - i);
                break;
            }
        }
    }

    private void declare(final String name) {
        if (scopes.isEmpty()) {
            return;
        } else {
            var scope = scopes.peek();
            scope.put(name, Boolean.FALSE);
        }
    }

    private void define(final String name) {
        if (scopes.isEmpty()) {
            return;
        } else {
            var scope = scopes.peek();
            scope.put(name, Boolean.TRUE);
        }
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }
}
