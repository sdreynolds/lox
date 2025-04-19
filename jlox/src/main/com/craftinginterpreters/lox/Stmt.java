package com.craftinginterpreters.lox;

import java.util.List;

sealed interface Stmt {}

record ExpressionStmt(Expr expression) implements Stmt {}
record PrintStmt(Expr expression) implements Stmt {}
record VarStmt(Token name, Expr initializer) implements Stmt {}
record BlockStmt(List<Stmt> statements)implements Stmt{}
