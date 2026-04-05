package gr.hua.dit.compilers;

import gr.hua.dit.compilers.Lexer;
import gr.hua.dit.compilers.parser;
import java_cup.runtime.Symbol;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {
  public static void main(String[] args) {

    // Check if user provided input file
    if (args.length != 1) {
      System.err.println("Usage: java gr.hua.dit.compilers.Main <input_file.tony>");
      System.exit(1);
    }

    try {
      // Initialize Lexer and connect it to input file
      Lexer lexer = new Lexer(new FileReader(args[0]));
      Symbol token;

      System.out.println("=========================================================");
      System.out.println("            Starting Lexical Analysis (Tony)             ");
      System.out.println("=========================================================");

      // Read tokens until EOF is reached
      do {
        token = lexer.next_token();

        // Attempt to retrieve string name of token from sym.java
        String tokenName;
        try {
          tokenName = sym.terminalNames[token.sym];
        } catch (Exception e) {
          // Fallback to numeric ID if terminalNames missing
          tokenName = "UNKNOWN";
        }

        // Print using formatting for perfect column alignment
        // %-4d  : Prints the integer left-aligned in a 4-character wide column
        // %-15s : Prints the string left-aligned in a 15-character wide column
        System.out.printf("Token ID: %-4d | Token: %-15s", token.sym, tokenName);

        // If token carries a value, print it
        if (token.value != null) {
          System.out.printf(" | Value: %s", token.value);
        }
        System.out.println();

      } while (token.sym != sym.EOF);

      System.out.println("=========================================================");
      System.out.println("Lexical analysis completed successfully!");

      // Exit code 0 --> Success
      System.exit(0);

    } catch (FileNotFoundException e) {
      System.err.println("Error: The file '" + args[0] + "' was not found.");
      System.exit(1);

    } catch (RuntimeException | Error e) {
      // Catch lexical errors thrown by Lexer
      System.err.println(e.getMessage());
      System.exit(1);

    } catch (Exception e) {
      // Catch any other exceptions
      System.err.println("An unexpected error occurred: " + e.getMessage());
      System.exit(1);
    }
  }
}
