package com.craftinginterpreters.lox;

import java.util.List;

class Interpreter {
    private Environment environment = new Environment();

    void interpret(final List<Stmt> statements ) {
        try {
            for (var statement: statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    void execute(final Stmt statement) {
        switch(statement) {
        case ExpressionStmt(var basicExpr) -> evaluate(basicExpr);
        case PrintStmt(var stmtToPrint) -> {
            final var value = evaluate(stmtToPrint);
            System.out.println(stringify(value));
        }
        case VarStmt(Token(var _type, var varName, var _literal, var _line), var varInit) when varInit != null ->
            environment.define(varName, evaluate(varInit));
        case VarStmt(Token(var _type, var nilName, var _literal, var _line), var _nil) ->
            environment.define(nilName, null);

        case BlockStmt(var statements) -> executeBlock(statements, new Environment(environment));

        case IfStmt(var condition, var thenBranch, var elseBranch) -> {
            if (Interpreter.isTruthy(evaluate(condition))) {
                execute(thenBranch);
            } else {
                elseBranch.ifPresent(stmt -> execute(stmt));
            }
        }

        };
    }

    void executeBlock(final List<Stmt> statements, final Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            statements.forEach(stmt -> execute(stmt));
        } finally {
            this.environment = previous;
        }
    }

    Object evaluate(final Expr expr) {
        return switch(expr) {
        case LiteralExpr(var value) -> value;
        case GroupingExpr(var expressions) -> evaluate(expressions);
        case UnaryExpr(Token unaryOperator, var unaryExpr) -> {
            final var right = evaluate(unaryExpr);

            yield switch(unaryOperator.type()) {
            case TokenType.MINUS -> {
                checkNumberOperand(unaryOperator, right);
                yield -(double)right;
            }
            case TokenType.BANG -> !Interpreter.isTruthy(right);

            // Hmm
            default -> null;
            };
        }
        case BinaryExpr(var leftExpr, var operator, var rightExpr) -> {
            final var left = evaluate(leftExpr);
            final var right = evaluate(rightExpr);

            yield switch(operator.type()) {
            case TokenType.MINUS -> {
                checkNumberOperands(operator, left, right);
                yield (double)left - (double)right;
            }
            case TokenType.SLASH -> {
                checkNumberOperands(operator, left, right);
                yield (double)left / (double)right;
            }
            case TokenType.STAR -> {
                checkNumberOperands(operator, left, right);
                yield (double)left * (double)right;
            }

            case TokenType.PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    yield (double)left + (double)right;
                }
                else if (left instanceof String && right instanceof String) {
                    yield left.toString() + right.toString();
                }
                throw new RuntimeError(operator, "Operands must be two numbers or two strings.");
            }

            case TokenType.GREATER -> {
                checkNumberOperands(operator, left, right);
                yield (double)left > (double)right;
            }
            case TokenType.LESS -> {
                checkNumberOperands(operator, left, right);
                yield (double)left < (double)right;
            }
            case TokenType.GREATER_EQUAL -> {
                checkNumberOperands(operator, left, right);
                yield (double)left >= (double)right;
            }
            case TokenType.LESS_EQUAL -> {
                checkNumberOperands(operator, left, right);
                yield (double)left <= (double)right;
            }

            case TokenType.BANG_EQUAL -> !Interpreter.isEqual(left, right);
            case TokenType.EQUAL_EQUAL -> Interpreter.isEqual(left, right);

            // Rest of the operators are not possible
            default -> null;
            };
        }
        case VariableExpr(var nameToken) -> environment.get(nameToken);

        case AssignExpr(var assignName, var value) -> {
            final var evaluation = evaluate(value);
            environment.assign(assignName, evaluation);
            // Return the value
            yield evaluation;
        }

        case LogicalExpr(var leftLogical, var logicalOperator, var rightLogical) -> {
            final var left = evaluate(leftLogical);

            if (logicalOperator.type() == TokenType.OR) {
                if (isTruthy(left)) {
                    yield left;
                }
            } else {
                if (!isTruthy(left)) {
                    yield left;
                }
            }
            yield evaluate(rightLogical);
        }

        };
    }

    static private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null) {
            return false;
        }
        return a.equals(b);
    }

    static private boolean isTruthy(final Object object) {
        if (object == null) {
            return false;
        } else if (object instanceof Boolean) {
            return (boolean) object;
        } else {
            return true;
        }
    }

    static private String stringify(Object object) {
        if (object == null) {
            return "nil";
        }
        else if (object instanceof Double) {
            final var text = object.toString();
            if (text.endsWith(".0")) {
                return text.substring(0, text.length() - 2);
            }
            return text;
        } else {
            return object.toString();
        }
    }

    static private void checkNumberOperand(final Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    static private void checkNumberOperands(final Token operator, final Object left, final Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operands must be numbers.");
    }
}
