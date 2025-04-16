package com.craftinginterpreters.lox;

record Name(String fName, String lName, String mName) {};

class AstPrinter {
    static String print(Expr expr) {
        return switch(expr) {
        case LiteralExpr(var value) when value == null -> "nil";
        case LiteralExpr(var value) -> value.toString();

        case UnaryExpr(Token(var type, var lexeme, var _literal, var line), var right) ->
            AstPrinter.parenthesize(lexeme, right);

        case BinaryExpr(var left, Token(var type, var lexeme, var literal, var line), var right) ->
            AstPrinter.parenthesize(lexeme, left, right);

        case GroupingExpr(var groupedExpr) -> AstPrinter.parenthesize("group", groupedExpr);
        };
    }

    static String parenthesize(final String name, Expr... exprs) {
        final StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (var expr: exprs) {
            builder.append(" ");
            builder.append(AstPrinter.print(expr));
        }
        builder.append(")");
        return builder.toString();
    }
}
