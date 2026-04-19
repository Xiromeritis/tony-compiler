package gr.hua.dit.compilers;

import java.io.File;

/**
 * The entry point of the Tony Compiler.
 * Acts as a CLI (Command Line Interface) router that parses user arguments
 * and directs the execution flow to either the Lexical or Syntax analysis phase.
 */
public class Main {
  public static void main(String[] args) {

    if (args.length == 0) {
      System.err.println("Usage: java -jar target/compiler-0.3.jar [--lex | --parse] <input_file.tony>");
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
        // If no flag is provided, run both sequentially!
        LexicalAnalysis.run(filePath);
        System.out.println("\n");
        SyntaxAnalysis.run(filePath);
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
