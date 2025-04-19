package com.craftinginterpreters.lox;

import java.util.List;

class Interpreter {
    static void interpret(final List<Stmt> statements ) {
        try {
            for (var statement: statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    static void execute(final Stmt statement) {
        switch(statement) {
        case ExpressionStmt(var basicExpr) -> Interpreter.evaluate(basicExpr);
        case PrintStmt(var stmtToPrint) -> {
            final var value = Interpreter.evaluate(stmtToPrint);
            System.out.println(stringify(value));
        }
        };
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

    static Object evaluate(final Expr expr) {
        return switch(expr) {
        case LiteralExpr(var value) -> value;
        case GroupingExpr(var expressions) -> Interpreter.evaluate(expressions);
        case UnaryExpr(Token unaryOperator, var unaryExpr) -> {
            final var right = Interpreter.evaluate(unaryExpr);

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
            final var left = Interpreter.evaluate(leftExpr);
            final var right = Interpreter.evaluate(rightExpr);

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
