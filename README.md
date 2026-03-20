# Java Compiler Skeleton

This project is a template for starting building a compiler in Java. It provides a foundational skeleton configured with the necessary tools and libraries to implement lexical analysis, parsing, and code generation.

## Dependencies

The project is built around the following key tools:
- **Java 21**
- **Maven**
- **JFlex**: A lexical analyzer generator for Java, used to tokenize source files.
- **Java CUP**: A LALR parser generator for Java, working with JFlex.
- **[OW2-ASM](https://asm.ow2.io/)**: A framework for working with Java bytecode.
- **[LLVM](https://bytedeco.org/javacpp-presets/llvm/apidocs/)**: A collection of modular and reusable compiler and toolchain technologies.

## Project Structure

- `src/main/java/`: Contains your Java source code (e.g., `Main.java` which serves as the entry point).
- `src/main/jflex/`: Contains the lexical analyzer rules (`lexer.flex`).
- `src/main/cup/`: Contains the parser grammar rules (`parser.cup`).
- `pom.xml`: The Maven configuration file defining dependencies and plugins.

## How to Build

We use the Maven Wrapper so you don't need to manually install Maven on your machine.

To compile the project and generate the lexer and parser classes from your JFlex and CUP definitions, run:

```bash
./mvnw clean package
```

*(On Windows, use `mvnw.cmd clean package`)*

During the `package` phase, Maven will:
1. Generate the scanner (`Lexer.java`) from the `.flex` file.
2. Generate the parser (`parser.java` and `sym.java`) from the `.cup` file.
3. Compile all Java sources (including the generated ones).
4. Create an executable "fat JAR" containing all required dependencies in the `target/` directory.

## How to Execute

After a successful build, a shaded JAR named `compiler-0.1.jar` will be generated in the `target` folder.

You can execute the compiler by running:

```bash
java -jar target/compiler-0.1.jar
```

*(Note: The main entry point is `gr.hua.dit.compilers.Main`. You will need to write the logic in this class to open a file, pass it to the `Lexer`, and invoke the `parser`.)*
