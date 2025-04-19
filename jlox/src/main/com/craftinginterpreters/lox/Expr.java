package com.craftinginterpreters.lox;

sealed interface Expr {}

record BinaryExpr(Expr left, Token operator, Expr right) implements Expr {}

record GroupingExpr(Expr expressions) implements Expr {}

record LiteralExpr(Object value) implements Expr {}

record UnaryExpr(Token operator, Expr right) implements Expr {}

record VariableExpr(Token name) implements Expr {}

record AssignExpr(Token name, Expr value) implements Expr {}

record LogicalExpr(Expr left, Token operator, Expr right) implements Expr {}
