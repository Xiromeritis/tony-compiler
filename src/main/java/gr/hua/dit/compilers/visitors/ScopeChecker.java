package gr.hua.dit.compilers.visitors;

import gr.hua.dit.compilers.ast.*;
import gr.hua.dit.compilers.symbols.SymbolEntry;
import gr.hua.dit.compilers.symbols.SymbolTable;
import gr.hua.dit.compilers.SemanticError;

public class ScopeChecker extends AbstractVisitor {

  private final SymbolTable symbolTable;

  public ScopeChecker() {
    this.symbolTable = new SymbolTable();
  }

  @Override
  public void visit(ProgramNode node) {
    symbolTable.enterScope(); // Open global scope

    if (node.getMainFunc() != null) {
      node.getMainFunc().accept(this);
    }

    symbolTable.exitScope(); // Close global scope
  }

  @Override
  public void visit(FuncDefNode node) {
    String funcName = node.getHeader().getName();

    // Add function name to current scope
    try {
      symbolTable.addEntry(funcName, node.getHeader().getReturnType());
    } catch (SemanticError e) {
      System.err.println("[Semantic Error] " + e.getMessage());
    }

    // Open new scope for the function body
    symbolTable.enterScope();

    // 3. Visit parameters
    if (node.getHeader().getFormals() != null) {
      for (FormalNode f : node.getHeader().getFormals()) {
        f.accept(this);
      }
    }

    // Visit local declarations
    if (node.getLocalDecls() != null) {
      for (ASTNode decl : node.getLocalDecls()) {
        decl.accept(this);
      }
    }

    // Visit statements
    if (node.getStmts() != null) {
      for (Stmt stmt : node.getStmts()) {
        stmt.accept(this);
      }
    }

    // Close function scope
    symbolTable.exitScope();
  }

  @Override
  public void visit(VarDefNode node) {
    // Register variables using addEntry
    for (String id : node.getIds()) {
      try {
        symbolTable.addEntry(id, node.getType());
      } catch (SemanticError e) {
        System.err.println("[Semantic Error] " + e.getMessage());
      }
    }
  }

  @Override
  public void visit(FormalNode node) {
    // Register parameters using addEntry
    for (String id : node.getIds()) {
      try {
        symbolTable.addEntry(id, node.getType());
      } catch (SemanticError e) {
        System.err.println("[Semantic Error] " + e.getMessage());
      }
    }
  }

  @Override
  public void visit(IdNode node) {
    if (!symbolTable.exists(node.getName())) {
      System.err.println("[Semantic Error] Variable " + node.getName() + " is undefined");
    }
  }

  @Override
  public void visit(CallNode node) {
    // Check if function exists using getEntry
    try {
      SymbolEntry ignored = symbolTable.getEntry(node.getFunctionName()); // <-- Πρόσθεσε το "SymbolEntry ignored ="
    } catch (SemanticError e) {
      System.err.println("[Semantic Error] " + e.getMessage());
    }

    // Visit arguments
    if (node.getArguments() != null) {
      for (Expr arg : node.getArguments()) {
        arg.accept(this);
      }
    }
  }

  @Override
  public void visit(AssignStmt node) {
    if (node.getTarget() != null) node.getTarget().accept(this);
    if (node.getE() != null) node.getE().accept(this);
  }
}
