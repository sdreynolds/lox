package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) {
            System.exit(65);
        }
        else if (hadRuntimeError) {
            System.exit(70);
        }
    }
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
        }
    }
    private static void run(String source) {
        final var scanner = new Scanner(source);
        final var tokens = scanner.scanTokens();

        final var parser = new Parser(tokens);
        final var expr = parser.parse();

        if (hadError) {
            return;
        }
        System.out.println(AstPrinter.print(expr));
        Interpreter.interpret(expr);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void error(final Token token, final String message) {
        switch(token) {
        case Token(var eofToken, var _lexeme, var _literal, var eofLine) when eofToken == TokenType.EOF -> report(eofLine, " at end", message);
        case Token(var type, var lexeme, var _literal, var line) -> report(line, " at '" + lexeme + "'", message);
        }
    }

    static void runtimeError(final RuntimeError error) {
        final var msg = error.getMessage();
        final var line = error.token.line();
        System.err.println(msg + "\n[line " + line + "]");
        hadRuntimeError = true;
    }

    private static void report(int line, String where,
                               String message) {
        System.err.println(
            "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

}
