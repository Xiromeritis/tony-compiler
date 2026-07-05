package gr.hua.dit.compilers;

import gr.hua.dit.compilers.ast.ProgramNode;
import gr.hua.dit.compilers.visitors.ScopeChecker;
import gr.hua.dit.compilers.visitors.TypeChecker;

/*
 * Phase 3 & 4: Semantic Analysis.
 * This class orchestrates semantic validation by running the appropriate visitors.
 */
public class SemanticAnalysis {
  public static void run(ProgramNode root) {
    // --- Scope Checking ---
    System.out.println("\n=========================================================");
    System.out.println("          Phase 3: Semantic Analysis (Scope)             ");
    System.out.println("=========================================================");
    ScopeChecker scopeChecker = new ScopeChecker();
    root.accept(scopeChecker);
    System.out.println("Scope checking completed.");

    // --- Type Checking ---
    System.out.println("\n=========================================================");
    System.out.println("          Phase 4: Semantic Analysis (Types)             ");
    System.out.println("=========================================================");
    TypeChecker typeChecker = new TypeChecker();
    root.accept(typeChecker);
    System.out.println("Type checking completed.");
  }
}
