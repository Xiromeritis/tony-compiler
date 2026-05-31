# Tony Compiler (Phase 4: Semantic Analysis - Type Checking)

A multi-pass compiler for the **Tony** programming language, developed in Java using **JFlex** (Lexical Analysis) and **Java CUP** (Syntax Analysis).

This project is currently in **Phase 4 (Semantic Analysis - Full)**. It is capable of tokenizing source code, validating syntax, constructing the Abstract Syntax Tree (AST), enforcing strict scope rules, and performing comprehensive static **Type Checking**.

## Implemented Features

### Lexical Analysis (Phase 1)
- **Nested Comments:** Fully supports single-line (`%`) and nested multi-line (`<* ... *>`) comments.
- **Escape Sequences:** Correctly parses standard and hexadecimal (`\xNN`) escape sequences into native Java `Character` objects.

### Syntax Analysis (Phase 2)
- **Robust Syntax Parsing**: Enforces precedence rules and validates language grammar using a CUP-generated LALR parser.
- **AST Generation**: Converts raw code into a structured Object-Oriented tree hierarchy.

### Semantic Analysis - Scope Checking (Phase 3)
- **Dynamic Symbol Table**: Implements a Stack of Maps (`Stack<Map<String, SymbolEntry>>`) to elegantly handle nested block scopes.
- **Declaration Validation**: Detects and reports duplicate variable, parameter, or function declarations.
- **Reference Resolution**: Identifies the use of undefined variables or calls to undeclared functions.

### Semantic Analysis - Type Checking (Phase 4)
- **Strict Type Enforcement**: Validates mathematical, boolean, and relational operations to ensure type safety.
- **Function Validation**: Verifies argument counts, argument types, and strictly enforces expected return types (`return` statements) for both user-defined and built-in Tony functions (e.g., `puti`, `strlen`, `strcmp`).
- **Control Structures**: Ensures conditions in `if`, `elsif`, and `for` statements evaluate strictly to `bool`.
- **Compound Types (Arrays & Lists)**: Fully supports dynamic array indexing, list consing (`#`), and list traversal (`head`, `tail`, `nil?`).
- **Nil Handling**: Implements specialized logic to allow safe assignment and comparison of `nil` with any list type (`list[T]`).

## Build Instructions

This project uses **Maven** for dependency management and build automation. To build the compiler from source:
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

After a successful build, a shaded JAR named `compiler-0.5.jar` will be generated in the `target/` directory.

The compiler features a modern CLI that allows you to test different phases of the compilation process independently using flags.
To execute the analyzer(s) on a `.tony` source file, run:

**1. Phase 1 Only (Lexical Analysis):**

Prints a formatted table of all tokens found in the file.
```bash
java -jar target/compiler-0.5.jar --lex path/to/your/file.tony
```

> This will output a formatted table of all tokens identified in the source file, alongside their respective IDs and captured values.

**2. Phase 2 Only (Syntax Analysis & AST):**

Parses the file and prints the Abstract Syntax Tree.

```bash
java -jar target/compiler-0.5.jar --parse path/to/your/file.tony
```

> This will output the Abstract Syntax Tree (AST) generated from the source file.


**3. Full Pipeline (Lexical, Syntax, and Semantic Scope Checking):**

If no flag is provided, the compiler will sequentially run the Lexer, the Parser, and finally the Semantic Analyzer.

```bash
java -jar target/compiler-0.5.jar path/to/your/file.tony
```

> Outputs token tables, the AST, and performs a full semantic scan. Any scope or type errors (e.g., Type mismatch!) will be gracefully printed to standard error.

## License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.
