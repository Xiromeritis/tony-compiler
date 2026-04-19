package gr.hua.dit.compilers;

import java.io.FileReader;
import java_cup.runtime.Symbol;
import gr.hua.dit.compilers.ast.ProgramNode;

/**
 * Phase 2: Syntax Analysis.
 * This class uses the CUP-generated Parser to perform syntax validation.
 * It requests tokens from the Lexer, verifies them against the grammar rules,
 * and constructs the Abstract Syntax Tree (AST) for further semantic analysis.
 */
public class SyntaxAnalysis {

  public static void run(String filePath) {
    try {
      System.out.println("=========================================================");
      System.out.println("            Phase 2: Syntax Analysis (Tony)              ");
      System.out.println("=========================================================");

      Lexer lexer = new Lexer(new FileReader(filePath));
      parser p = new parser(lexer);
      Symbol result = p.parse();

      System.out.println("Syntax analysis completed successfully! No errors found.");

      if (result != null && result.value instanceof ProgramNode root) {
        System.out.println("\n=========================================================");
        System.out.println("                Abstract Syntax Tree (AST)               ");
        System.out.println("=========================================================");
        System.out.println(Main.formatAST(root.toString()));
      }

    } catch (Exception e) {
      System.err.println("\n[SYNTAX ERROR] Compilation Failed.");
      System.err.println("Details: " + e.getMessage());
      System.exit(1);
    }
  }
}
