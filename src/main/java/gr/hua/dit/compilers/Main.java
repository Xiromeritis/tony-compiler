package gr.hua.dit.compilers;

import java.io.File;
import java.io.FileReader;

// Σιγουρέψου ότι αυτά τα imports δείχνουν στους σωστούς φακέλους του project σου!
import gr.hua.dit.compilers.ast.ProgramNode;
import gr.hua.dit.compilers.visitors.ScopeChecker;
import gr.hua.dit.compilers.visitors.CodeGenerator;

/**
 * The entry point of the Tony Compiler.
 * Acts as a CLI (Command Line Interface) router that parses user arguments
 * and directs the execution flow.
 */
public class Main {
  public static void main(String[] args) {

    if (args.length == 0) {
      // Ενημερώθηκε σε compiler-1.0.jar
      System.err.println("Usage: java -jar target/compiler-1.0.jar [--lex | --parse] <input_file.tony>");
      System.exit(1);
    }

    // Parse arguments
    String flag = "";
    String filePath;

    if (args.length == 2) {
      flag = args[0];        // e.g., --lex
      filePath = args[1];   // e.g., test.tony
    } else {
      filePath = args[0];   // Default: just the file path
    }

    // Verify file exists before doing anything
    File file = new File(filePath);
    if (!file.exists()) {
      System.err.println("Error: The file '" + filePath + "' was not found.");
      System.exit(1);
    }

    // Route the request based on the flag
    switch (flag) {
      case "--lex":
        LexicalAnalysis.run(filePath);
        break;
      case "--parse":
        SyntaxAnalysis.run(filePath);
        break;
      default:
        // ==========================================
        // FULL COMPILATION PIPELINE (No flags)
        // ==========================================
        try {
          Lexer lexer = new Lexer(new FileReader(filePath));
          parser p = new parser(lexer);

          ProgramNode root = (ProgramNode) p.parse().value;

          ScopeChecker semantic = new ScopeChecker();
          root.accept(semantic);

          if (semantic.getErrorCount() > 0) {
            System.err.println("\n[COMPILATION HALTED] " + semantic.getErrorCount() + " semantic error(s) found.");
            System.exit(1);
          }

          // --- Code Generation ---
          System.out.println("\n=========================================================");
          System.out.println("             Phase 5: Code Generation (LLVM)             ");
          System.out.println("=========================================================");
          CodeGenerator codeGen = new CodeGenerator();
          codeGen.generate(root, "output.ll");

        } catch (Exception e) {
          System.err.println("\n[SYNTAX ERROR] Compilation Failed.");
          System.err.println("Details: " + e.getMessage());
          System.exit(1);
        }
        break;
    }
  }

  // Format AST for readability
  public static String formatAST(String raw) {
    String[] lines = raw.replace("\r", "").split("\n");
    StringBuilder result = new StringBuilder();
    int currentIndent = 0;

    for (String line : lines) {
      line = line.trim();
      if (line.isEmpty()) continue;

      int closeAtStart = 0;
      for (int i = 0; i < line.length(); i++) {
        char c = line.charAt(i);
        if (c == ')' || c == ']') {
          closeAtStart++;
        } else if (c != ',' && c != ' ') {
          break;
        }
      }

      int printIndent = Math.max(0, currentIndent - closeAtStart);
      result.repeat("    ", printIndent);
      result.append(line).append("\n");

      int openCount = 0;
      int closeCount = 0;
      boolean inQuotes = false;

      for (char c : line.toCharArray()) {
        if (c == '"') inQuotes = !inQuotes;
        if (!inQuotes) {
          if (c == '(' || c == '[') openCount++;
          else if (c == ')' || c == ']') closeCount++;
        }
      }
      currentIndent += (openCount - closeCount);
      if (currentIndent < 0) currentIndent = 0;
    }
    return result.toString();
  }
}
