package com.craftinginterpreters.lox;

class Interpreter {
    static Object interpret(final Expr expr) {
        return switch(expr) {
        case LiteralExpr(var value) -> value;
        case GroupingExpr(var expressions) -> Interpreter.interpret(expressions);
        case UnaryExpr(Token unaryOperator, var unaryExpr) -> {
            final var right = Interpreter.interpret(unaryExpr);

            yield switch(unaryOperator.type()) {
            case TokenType.MINUS -> -(double)right;
            case TokenType.BANG -> !Interpreter.isTruthy(right);

            // Hmm
            default -> null;
            };
        }
        case BinaryExpr(var leftExpr, var operator, var rightExpr) -> {
            final var left = Interpreter.interpret(leftExpr);
            final var right = Interpreter.interpret(rightExpr);

            yield switch(operator.type()) {
            case TokenType.MINUS -> (double)left - (double)right;
            case TokenType.SLASH -> (double)left / (double)right;
            case TokenType.STAR -> (double)left * (double)right;

            case TokenType.PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    yield (double)left + (double)right;
                }
                else {
                    yield left.toString() + right.toString();
                }
            }

            case TokenType.GREATER -> (double)left > (double)right;
            case TokenType.LESS -> (double)left < (double)right;
            case TokenType.GREATER_EQUAL -> (double)left >= (double)right;
            case TokenType.LESS_EQUAL -> (double)left <= (double)right;

            case TokenType.BANG_EQUAL -> !Interpreter.isEqual(left, right);
            case TokenType.EQUAL_EQUAL -> Interpreter.isEqual(left, right);

            // Rest of the operators are not possible
            default -> null;
            };
        }

        };
    }

    static boolean isEqual(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null) {
            return false;
        }
        return a.equals(b);
    }

    static boolean isTruthy(final Object object) {
        if (object == null) {
            return false;
        } else if (object instanceof Boolean) {
            return (boolean) object;
        } else {
            return true;
        }
    }
}
