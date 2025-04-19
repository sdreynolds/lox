package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
    private final Map<String, Object> values = new HashMap<>();

    void define(final String name, final Object value) {
        values.put(name, value);
    }

    Object get(final Token name) {
        if (values.containsKey(name.lexeme())) {
            return values.get(name.lexeme());
        } else {
            throw new RuntimeError(name, "Undefined var '" + name.lexeme() + "'.");
        }
    }
}
