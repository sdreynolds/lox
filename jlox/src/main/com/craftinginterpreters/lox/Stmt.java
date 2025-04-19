package com.craftinginterpreters.lox;

sealed interface Stmt {}

record ExpressionStmt(Expr expression) implements Stmt {}
record PrintStmt(Expr expression) implements Stmt {}
record VarStmt(Token name, Expr initializer) implements Stmt {}
