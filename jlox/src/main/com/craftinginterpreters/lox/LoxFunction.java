package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
    private final FunctionStmt declaration;
    private final Environment closure;

    LoxFunction(FunctionStmt declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        final var environment = new Environment(closure);
        for (int i = 0; i < declaration.params().size(); i++ ) {
            environment.define(
                declaration.params().get(i).lexeme(),
                arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.body(), environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }
        return null;
    }

    @Override
    public int arity() {
        return declaration.params().size();
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name().lexeme() + ">";
    }
}
