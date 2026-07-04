# Tony Compiler (Phase 5: Code Generation - LLVM IR)

A multi-pass compiler for the **Tony** programming language, developed in Java using **JFlex** (Lexical Analysis) and **Java CUP** (Syntax Analysis).

This project is currently in **Phase 5 (Code Generation)**. It is capable of tokenizing source code, validating syntax, constructing the Abstract Syntax Tree (AST), enforcing strict scope rules, performing comprehensive static Type Checking, and ultimately translating the program into **LLVM Intermediate Representation (IR)**.

## Implemented Features

### Lexical Analysis (Phase 1)
- **Nested Comments:** Fully supports single-line (`%`) and nested multi-line (`<* ... *>`) comments.
- **Escape Sequences:** Correctly parses standard and hexadecimal (`\xNN`) escape sequences into native Java Character objects.

### Syntax Analysis (Phase 2)
- **Robust Syntax Parsing**: Enforces precedence rules and validates language grammar using a CUP-generated LALR parser.
- **AST Generation**: Converts raw code into a structured Object-Oriented tree hierarchy.

### Semantic Analysis - Scope Checking (Phase 3)
- **Dynamic Symbol Table**: Implements a Stack of Maps (`Stack<Map<String`, `SymbolEntry>>`) to elegantly handle nested block scopes.
- **Declaration Validation**: Detects and reports duplicate variable, parameter, or function declarations.
- **Reference Resolution**: Identifies the use of undefined variables or calls to undeclared functions.

### Semantic Analysis - Type Checking (Phase 4)
- **Strict Type Enforcement**: Validates mathematical, boolean, and relational operations to ensure type safety.
- **Function Validation**: Verifies argument counts, argument types, and strictly enforces expected return types (return statements) for both user-defined and built-in Tony functions.
- **Compound Types**: Fully supports dynamic array indexing, list consing (`#`), and list traversal (`head`, `tail`, `nil?`).

### Code Generation - LLVM IR (Phase 5)
- **LLVM IR Translation**: Translates the checked AST into optimized LLVM Intermediate Representation (`.ll`).
- **Memory Management**: Handles dynamic heap allocations (`malloc`) for runtime structures like arrays and lists.
- **Pass-by-Reference (ref)**: Safely maps Tony's ref parameters to LLVM pointers, allowing in-place variable modifications (e.g., `swap` function).
- **Control Flow**: Translates nested `if`, `elsif`, `else`, and `for` loops into proper LLVM Basic Blocks and Branching instructions.
- **Function Flattening**: Automatically extracts and flattens nested function definitions to comply with standard LLVM/C architecture.
- **String & Escape Handling**: Generates properly formatted, null-terminated globally allocated strings for native execution.

## Prerequisites & Dependencies

To build and run this compiler, you will need the following tools installed on your system:
- **Java Development Kit (JDK):** Version 11 or higher.
- **Apache Maven:** For dependency management and project building.
- **LLVM & Clang:** Required for compiling and linking the generated LLVM IR.
- **JFlex & Java CUP:** Used for Lexical and Syntax analysis (Automatically downloaded and managed by Maven).

## OS-Specific Setup

Depending on your operating system, follow these instructions to install the required tools:

**Linux (Ubuntu / Debian)**
Open your terminal and run:
```bash
sudo apt update
sudo apt install default-jdk maven clang llvm
```

**macOS**
Using Homebrew, open your terminal and run:
```bash
brew install openjdk maven llvm
```

**Windows**
Windows does not natively support the Clang/LLVM toolchain easily. It is highly recommended to use WSL (Windows Subsystem for Linux):
1. Open PowerShell and type:
```powershell
wsl
```
2. Once inside the Linux environment, run the Linux (Ubuntu/Debian) commands listed above to install Java and LLVM.
3. Navigate to your project directory (e.g., `cd /mnt/c/Users/YourName/tony-compiler`) and run the compiler from there.

## Project Structure
```text
tony-compiler/
├── src/main/java/      # Compiler source code (AST, Visitors, Symbol Table)
├── src/main/jflex/     # Lexer specification (lexer.flex)
├── src/main/cup/       # Parser specification (parser.cup)
├── lib.c               # C standard library wrapper for Tony built-ins
├── compile.sh          # Automation script for batch compilation and execution
├── pom.xml             # Maven configuration and dependencies
└── README.md           # Project documentation
```

## Build Instructions

This project uses **Maven** for dependency management and build automation. To build the compiler from source:
```bash
./mvnw clean package
```

*(On Windows, use mvnw.cmd clean package)*

During the package phase, Maven will generate the Scanner and Parser, compile all Java sources, and create an executable "fat JAR" containing all required dependencies.

## Usage & CLI Flags

After a successful build, a shaded JAR named `compiler-1.0.jar` will be generated in the `target/` directory.

The compiler features a modern CLI that allows you to test different phases of the compilation process independently using flags. To execute the analyzer(s) on a `.tony` source file, run:

**1. Phase 1 Only (Lexical Analysis):**
```bash
java -jar target/compiler-1.0.jar --lex path/to/your/file.tony
```

> Outputs a formatted table of all tokens identified in the source file, alongside their IDs.

**2. Phase 2 Only (Syntax Analysis & AST):**
```bash
java -jar target/compiler-1.0.jar --parse path/to/your/file.tony
```

> Outputs the Abstract Syntax Tree (AST) generated from the source file.

**3. Full Pipeline (Compilation to LLVM):**
If no flag is provided, the compiler will sequentially run Lexical, Syntax, Semantic Analysis, and Code Generation.
```bash
java -jar target/compiler-1.0.jar path/to/your/file.tony
```

> Outputs token tables, the AST, performs a semantic scan, and successfully generates an `output.ll` file in the working directory.

## Creating & Running an Executable

To execute the compiled Tony program, you need to link the generated LLVM IR (`output.ll`) with the standard library wrapper (`lib.c`).

**Method A: Using Clang (Generates a native Executable)**

1. Generate the LLVM IR
```bash
java -jar target/compiler-1.0.jar file.tony
```

2. Compile to a native executable using Clang
```bash
clang output.ll lib.c -o program
```

3. Run the program
```bash
4. ./program
```

**Method B: Using LLVM Interpreter (lli)**

1. Compile the C library to LLVM IR (Run once)
```bash
clang -S -emit-llvm lib.c -o lib.ll
```

2. Link your generated code with the library IR
```bash
llvm-link output.ll lib.ll -S -o combined.ll
```

3. Execute directly via the LLVM interpreter
```bash
lli combined.ll
```

## Automation Script (compile.sh)

For your convenience, a Bash script is provided to automatically compile your .tony files to LLVM IR, link them with the C library, and run them using the LLVM Interpreter (lli).

**How to run it:**
Make the script executable and pass one or multiple `.tony` files as arguments:
```bash
chmod +x compile.sh
./compile.sh path/to/file0.tony path/to/file1.tony
```

## License

This project is licensed under the MIT License – see the [LICENSE](LICENSE) file for details.
