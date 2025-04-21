package com.craftinginterpreters.lox;

import java.util.List;
import java.util.Optional;

sealed interface Stmt {}

record ExpressionStmt(Expr expression) implements Stmt {}
record PrintStmt(Expr expression) implements Stmt {}
record VarStmt(Token name, Expr initializer) implements Stmt {}
record BlockStmt(List<Stmt> statements)implements Stmt{}
record IfStmt(Expr condition, Stmt thenBranch, Optional<Stmt> elseBranch) implements Stmt {}
record WhileStmt(Expr condition, Stmt body) implements Stmt {}
record BreakStmt() implements Stmt{}
record ContinueStmt() implements Stmt{}
record FunctionStmt(Token name, List<Token> params, List<Stmt> body) implements Stmt {}
record ReturnStmt(Token keyword, Optional<Expr> value) implements Stmt {}
