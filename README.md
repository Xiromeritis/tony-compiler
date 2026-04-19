# Tony Compiler (Phase 2: Syntax Analysis)

A multi-pass compiler for the **Tony** programming language, developed in Java using **JFlex** (Lexical Analysis) and **Java CUP** (Syntax Analysis).

This project is currently in **Phase 2 (Syntax Analysis)** and is capable of tokenizing source code, validating its syntax against the language grammar, and constructing a fully formatted Abstract Syntax Tree (AST).

## Implemented Features

### Lexical Analysis

The Lexer accurately tokenizes Tony source code and includes several advanced constraints and safety checks:
- **Nested Comments:** Fully supports single-line (`%`) and nested multi-line (`<* ... *>`) comments, including EOF detection for unclosed comments.
- **Strict Integer Bounds:** Handles 16-bit integer limits (up to `32767`, deliberately allowing `32767 + 1` to support negative values at the parser level).
- **String & Character Parsing:** Correctly parses and unescapes standard (`\n`, `\t`) and hexadecimal (`\xNN`) escape sequences into native Java `Character` objects.
- **Identifier Constraints:** Enforces a maximum length of 64 characters for variable and function names.
- **Formatted Output:** Prints tokenized output in a clean, perfectly aligned console table for easy debugging.

### Syntax Analysis

* **Robust Syntax Parsing**: Enforces precedence rules (e.g., Unary minus vs. Binary minus) and validates language grammar.
* **AST Generation**: Converts raw code into a structured Object-Oriented tree hierarchy.
* **Pretty-Printed AST**: Features a custom-built string formatter that automatically indents nested nodes (Functions, Loops, IF-blocks) without relying on memory addresses (`@hashcodes`).
* **Advanced Error Handling**: Provides clear error messages with **line and column tracking** for syntax errors.

## Dependencies

The project is built around the following key tools:
- **Java 21**
- **Maven** (using Maven Wrapper)
- **JFlex**: Generates the lexical analyzer (`Lexer.java`).
- **Java CUP**: Generates the parser interface and symbol tables (`sym.java`).

## Project Structure

The compiler architecture follows strict Separation of Concerns:

* **`src/main/jflex/lexer.flex`**: The lexical specification file. Defines the rules for generating tokens.
* **`src/main/cup/parser.cup`**: The LALR grammar specification file. Defines the syntax rules and AST node generation.
* **`gr/hua/dit/compilers/ast/`**: Contains the hierarchy of Abstract Syntax Tree (AST) nodes (e.g., `IfStmt`, `BinaryNode`, `FuncDefNode`).
* **`gr/hua/dit/compilers/LexicalAnalysis.java`**: Handles Phase 1 (Tokenization).
* **`gr/hua/dit/compilers/SyntaxAnalysis.java`**: Handles Phase 2 (Parsing & AST construction).
* **`gr/hua/dit/compilers/Main.java`**: The CLI router that processes user arguments and triggers the appropriate compilation phases.

## How to Build

We use the Maven Wrapper so you don't need to manually install Maven on your machine. To compile the project and generate the lexer and parser classes, run:

```bash
./mvnw clean package
```

*(On Windows, use `mvnw.cmd clean package`)*

During the `package` phase, Maven will:
1. Generate the scanner (`Lexer.java`) from the `.flex` file.
2. Generate the parser (`parser.java` and `sym.java`) from the `.cup` file.
3. Compile all Java sources.
4. Create an executable "fat JAR" containing all required dependencies.

## Usage & CLI Flags

After a successful build, a shaded JAR named `compiler-0.3.jar` will be generated in the `target/` directory.

The compiler features a modern CLI that allows you to test different phases of the compilation process independently using flags.
To execute the analyzer(s) on a `.tony` source file, run:

**1. Phase 1 Only (Lexical Analysis):**

Prints a formatted table of all tokens found in the file.
```bash
java -jar target/compiler-0.3.jar --lex path/to/your/file.tony
```

> This will output a formatted table of all tokens identified in the source file, alongside their respective IDs and captured values.

**2. Phase 2 Only (Syntax Analysis & AST):**

Parses the file and prints the Abstract Syntax Tree.

```bash
java -jar target/compiler-0.3.jar --parse path/to/your/file.tony
```

> This will output the Abstract Syntax Tree (AST) generated from the source file.


**3. Full Compilation (Both Phases):**

If no flag is provided, the compiler will sequentially run the Lexer, followed by the Parser.

```bash
java -jar target/compiler-0.3.jar path/to/your/file.tony
```

> This will output both the Lexer and Parser output, alongside the Abstract Syntax Tree.

## License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.
