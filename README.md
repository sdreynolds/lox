# Lox Programming Language

A complete implementation of the Lox programming language following along with Robert Nystrom's excellent book [**Crafting Interpreters**](https://craftinginterpreters.com/).

## About

This repository contains two complete implementations of the Lox programming language:

- **jlox**: A tree-walk interpreter written in Java (Part II of the book)
- **clox**: A bytecode virtual machine written in C (Part III of the book)

Lox is a small, dynamically-typed scripting language designed to demonstrate core programming language concepts including:

- Lexical analysis and parsing
- Abstract syntax trees
- Tree-walk interpretation
- Bytecode compilation and virtual machines
- Garbage collection
- Object-oriented programming with classes and inheritance
- Closures and first-class functions

## Language Features

Lox supports the following features:

- **Data Types**: Numbers (double-precision floating point), strings, booleans, and nil
- **Variables**: Dynamic typing with lexical scoping
- **Control Flow**: `if/else` statements, `while` and `for` loops
- **Functions**: First-class functions with closures
- **Classes**: Object-oriented programming with single inheritance
- **Built-ins**: `print` statement and `clock()` function

### Example Lox Code

```lox
// Variables and arithmetic
var a = 1;
var b = 2;
print a + b; // 3

// Functions
fun fib(n) {
  if (n <= 1) return n;
  return fib(n - 2) + fib(n - 1);
}

print fib(10); // 55

// Classes and inheritance
class Breakfast {
  init(meat, bread) {
    this.meat = meat;
    this.bread = bread;
  }

  serve(who) {
    print "Enjoy your " + this.meat + " and " + this.bread + ", " + who + ".";
  }
}

var breakfast = Breakfast("bacon", "toast");
breakfast.serve("you"); // Enjoy your bacon and toast, you.
```

## Building and Running

This project uses Bazel as the build system.

### Prerequisites

- Java 21+ (for jlox)
- GCC or Clang (for clox)
- Bazel

### Running jlox (Java Implementation)

```bash
# Build
bazel build //jlox/src/main/com/craftinginterpreters/lox:lox

# Run interactively (REPL)
bazel run //jlox/src/main/com/craftinginterpreters/lox:lox

# Run a Lox file
bazel run //jlox/src/main/com/craftinginterpreters/lox:lox -- path/to/script.lox
```

### Running clox (C Implementation)

```bash
# Build
bazel build //clox/src:main

# Run interactively (REPL)
bazel run //clox/src:main

# Run a Lox file
bazel run //clox/src:main -- path/to/script.lox
```

## Testing

Both implementations include comprehensive test suites:

```bash
# Run all jlox tests
bazel test //jlox/src/test/...

# Run all clox tests
bazel test //clox/test/...

# Run specific test suites
bazel test //jlox/src/test/com/craftinginterpreters/lox:FunctionTest
bazel test //clox/test:calculator_test
```

## Project Structure

```
├── jlox/                    # Java tree-walk interpreter
│   └── src/
│       ├── main/            # Core interpreter implementation
│       └── test/            # JUnit test suites
├── clox/                    # C bytecode virtual machine
│   ├── src/                 # VM implementation
│   └── test/                # Test programs and expected outputs
├── BUILD.bazel              # Root Bazel build file
├── MODULE.bazel             # Bazel module configuration
└── README.md
```

### Key Components

**jlox** (Java Implementation):
- `Scanner.java` - Lexical analysis
- `Parser.java` - Recursive descent parser
- `Expr.java` / `Stmt.java` - AST node definitions
- `Interpreter.java` - Tree-walk interpreter
- `Resolver.java` - Variable resolution and semantic analysis
- `Environment.java` - Variable scoping

**clox** (C Implementation):
- `scanner.c` - Lexical analysis
- `compiler.c` - Single-pass compiler to bytecode
- `chunk.c` - Bytecode representation
- `vm.c` - Stack-based virtual machine
- `value.c` - Dynamic value system
- `object.c` - Object representation
- `memory.c` - Garbage collector

## Learning Journey

This implementation follows the progression in "Crafting Interpreters":

1. **Part I**: Introduction to language implementation concepts
2. **Part II**: Building jlox - a tree-walk interpreter that directly executes the AST
3. **Part III**: Building clox - a more efficient bytecode virtual machine

The book provides an excellent foundation for understanding how programming languages work under the hood, from tokenization and parsing through interpretation and garbage collection.

## Acknowledgments

This project is based on the fantastic book [**Crafting Interpreters**](https://craftinginterpreters.com/) by Robert Nystrom. The book provides clear explanations and step-by-step guidance for building a complete programming language from scratch.