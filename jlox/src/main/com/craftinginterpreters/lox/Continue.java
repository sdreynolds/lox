package com.craftinginterpreters.lox;

final class Continue extends RuntimeException {
    Continue() {
        super(null, null, false, false);
    }
}
