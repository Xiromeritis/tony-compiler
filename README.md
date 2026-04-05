# Tony Compiler (Phase 1: Lexical Analysis)

This project implements a compiler for the **Tony** programming language in Java. It is currently in **Phase 1**, featuring a complete and robust Lexical Analyzer (Scanner) built with JFlex, accompanied by a dummy Java CUP parser to generate token identifiers.

## Implemented Features (Lexical Analysis)

The Lexer accurately tokenizes Tony source code and includes several advanced constraints and safety checks:
- **Nested Comments:** Fully supports single-line (`%`) and nested multi-line (`<* ... *>`) comments, including EOF detection for unclosed comments.
- **Strict Integer Bounds:** Handles 16-bit integer limits (up to `32767`, deliberately allowing `32767 + 1` to support negative values at the parser level).
- **String & Character Parsing:** Correctly parses and unescapes standard (`\n`, `\t`) and hexadecimal (`\xNN`) escape sequences into native Java `Character` objects.
- **Identifier Constraints:** Enforces a maximum length of 64 characters for variable and function names.
- **Formatted Output:** Prints tokenized output in a clean, perfectly aligned console table for easy debugging.

## Dependencies

The project is built around the following key tools:
- **Java 21**
- **Maven** (using Maven Wrapper)
- **JFlex**: Generates the lexical analyzer (`Lexer.java`).
- **Java CUP**: Generates the parser interface and symbol tables (`sym.java`).

## Project Structure

- `src/main/java/`: Contains the Java source code, including the `Main.java` entry point.
- `src/main/jflex/`: Contains the lexical analyzer rules (`lexer.flex`).
- `src/main/cup/`: Contains the parser grammar rules (`parser.cup`).
- `examples/`: (External) Contains `.tony` source code files for testing.

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

## How to Execute

After a successful build, a shaded JAR named `compiler-0.2.jar` will be generated in the `target/` directory.

To execute the lexical analyzer on a `.tony` source file, run:

```bash
java -jar target/compiler-0.2.jar path/to/your/file.tony
```

**Example Execution:**
```bash
java -jar target/compiler-0.2.jar ../examples/hello.tony
```

This will output a formatted table of all tokens identified in the source file, alongside their respective IDs and captured values.

## License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.
