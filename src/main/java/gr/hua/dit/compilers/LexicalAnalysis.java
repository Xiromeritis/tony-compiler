package gr.hua.dit.compilers;

import java.io.FileReader;
import java_cup.runtime.Symbol;

/**
 * Phase 1: Lexical Analysis.
 * This class uses the JFlex-generated Lexer to read the input file,
 * identify tokens based on the Tony language specification, and output them.
 * It acts as the tokenizer for the compiler.
 */
public class LexicalAnalysis {

  public static void run(String filePath) {
    try {
      System.out.println("=========================================================");
      System.out.println("            Phase 1: Lexical Analysis (Tony)             ");
      System.out.println("=========================================================");

      Lexer lexer = new Lexer(new FileReader(filePath));
      Symbol token;

      do {
        token = lexer.next_token();
        String tokenName;
        try {
          tokenName = sym.terminalNames[token.sym];
        } catch (Exception e) {
          tokenName = "UNKNOWN";
        }

        System.out.printf("Token ID: %-4d | Token: %-15s", token.sym, tokenName);
        if (token.value != null) {
          System.out.printf(" | Value: %s", token.value);
        }
        System.out.println();
      } while (token.sym != sym.EOF);

      System.out.println("=========================================================");
      System.out.println("Lexical analysis completed successfully!");

    } catch (Exception e) {
      System.err.println("\n[LEXICAL ERROR] " + e.getMessage());
      System.exit(1);
    }
  }
}
