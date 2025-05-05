package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class Environment {
    private final Map<String, Object> values = new HashMap<>();
    private final Optional<Environment> enclosing;

    Environment() {
        enclosing = Optional.empty();
    }

    Environment(final Environment enclosing) {
        this.enclosing = Optional.ofNullable(enclosing);
    }

    void define(final String name, final Object value) {
        values.put(name, value);
    }

    void assign(final Token name, final Object value) {
        if (values.containsKey(name.lexeme())) {
            values.put(name.lexeme(), value);
        } else if (enclosing.isPresent()) {
            enclosing.get().assign(name, value);
        } else {
            throw new RuntimeError(name, "Undefined variable '" + name.lexeme() + "'.");
        }
    }

    Object get(final Token name) {
        if (values.containsKey(name.lexeme())) {
            return values.get(name.lexeme());
        } else {
            return enclosing
                .map(env -> env.get(name))
                .orElseThrow(() -> new RuntimeError(name, "Undefined var '" + name.lexeme() + "'."));
        }
    }

    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme(), value);
    }

    Environment ancestor(int distance) {
        var environment = this;
        for (var i = 0; i < distance; i++) {
            environment = environment.enclosing.get();
        }
        return environment;
    }
}
