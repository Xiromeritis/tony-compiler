# Tony Compiler (Phase 3: Semantic Analysis)

A multi-pass compiler for the **Tony** programming language, developed in Java using **JFlex** (Lexical Analysis) and **Java CUP** (Syntax Analysis).

This project is currently in **Phase 3 (Semantic Analysis - Scope Checking)** and is capable of tokenizing source code, validating its syntax against the language grammar, constructing a fully formatted Abstract Syntax Tree (AST), and strictly enforcing language scope rules via a dynamic Symbol Table.

## Implemented Features

### Lexical Analysis (Phase 1)
The Lexer accurately tokenizes Tony source code and includes several advanced constraints and safety checks:
- **Nested Comments:** Fully supports single-line (`%`) and nested multi-line (`<* ... *>`) comments, including EOF detection for unclosed comments.
- **String & Character Parsing:** Correctly parses and unescapes standard (`\n`, `\t`) and hexadecimal (`\xNN`) escape sequences into native Java `Character` objects.
- **Identifier Constraints:** Enforces a maximum length of 64 characters for variable and function names.

### Syntax Analysis (Phase 2)
- **Robust Syntax Parsing**: Enforces precedence rules and validates language grammar using a CUP-generated LALR parser.
- **AST Generation**: Converts raw code into a structured Object-Oriented tree hierarchy.
- **Visual Output**: Pretty-prints the AST hierarchy in the console for easy debugging and structural verification.

### Semantic Analysis – Scope Checking (Phase 3)
- **Dynamic Symbol Table**: Implements a Stack of Maps (`Stack<Map<String, SymbolEntry>>`) to elegantly handle nested block scopes (global, functions, etc.).
- **Declaration Validation**: Detects and reports duplicate variable, parameter, or function declarations within the same scope.
- **Reference Resolution**: Identifies the use of undefined variables or calls to undeclared functions (`UndefinedVarException`, `SemanticError`).
- **Visitor Pattern Architecture**: Utilizes an `AbstractVisitor` (`ScopeChecker`) to traverse the AST without polluting the data classes with operational logic.

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

After a successful build, a shaded JAR named `compiler-0.4.jar` will be generated in the `target/` directory.

The compiler features a modern CLI that allows you to test different phases of the compilation process independently using flags.
To execute the analyzer(s) on a `.tony` source file, run:

**1. Phase 1 Only (Lexical Analysis):**

Prints a formatted table of all tokens found in the file.
```bash
java -jar target/compiler-0.4.jar --lex path/to/your/file.tony
```

> This will output a formatted table of all tokens identified in the source file, alongside their respective IDs and captured values.

**2. Phase 2 Only (Syntax Analysis & AST):**

Parses the file and prints the Abstract Syntax Tree.

```bash
java -jar target/compiler-0.4.jar --parse path/to/your/file.tony
```

> This will output the Abstract Syntax Tree (AST) generated from the source file.


**3. Full Pipeline (Lexical, Syntax, and Semantic Scope Checking):**

If no flag is provided, the compiler will sequentially run the Lexer, the Parser, and finally the Semantic Analyzer.

```bash
java -jar target/compiler-0.4.jar path/to/your/file.tony
```

> Outputs token tables, the AST, and performs a full semantic scan. Any scope errors (e.g., undefined variables) will be printed to standard error.

## License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.
