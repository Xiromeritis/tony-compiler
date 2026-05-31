package gr.hua.dit.compilers.visitors;

import gr.hua.dit.compilers.ast.*;
import gr.hua.dit.compilers.symbols.SymbolTable;
import gr.hua.dit.compilers.symbols.SymbolEntry;
import gr.hua.dit.compilers.SemanticError;
import java.util.Stack;

public class TypeChecker extends AbstractVisitor {

  private final SymbolTable symbolTable;

  // Stack to pass evaluated types from child nodes up to parent nodes
  private final Stack<String> typeStack;
  private final Stack<String> expectedReturnType;

  public TypeChecker() {
    this.symbolTable = new SymbolTable();
    this.typeStack = new Stack<>();
    this.expectedReturnType = new Stack<>();
  }

  // =========================================================
  // 1. SCOPE MANAGEMENT (Rebuilding the environment)
  // =========================================================

  @Override
  public void visit(ProgramNode node) {
    symbolTable.enterScope(); // Open global scope
    if (node.getMainFunc() != null) node.getMainFunc().accept(this);
    symbolTable.exitScope(); // Close global scope
  }

  @Override
  public void visit(FuncDefNode node) {
    // 1. Add the function to the symbol table
    try {
      symbolTable.addEntry(node.getHeader().getName(), node.getHeader().getReturnType());
    } catch (SemanticError ignored) {}

    // 2. Open a new scope for the function's local variables
    symbolTable.enterScope();

    // =================================================================
    // 3. THIS FIXES THE WARNING: Push the expected return type
    // =================================================================
    String retType = "void";
    if (node.getHeader().getReturnType() != null) {
      retType = node.getHeader().getReturnType().toString();
    }
    expectedReturnType.push(retType); // <-- ΕΔΩ ΜΠΑΙΝΕΙ ΣΤΗ ΣΤΟΙΒΑ!

    // 4. Visit formal parameters, local declarations, and statements
    if (node.getHeader().getFormals() != null) {
      for (FormalNode f : node.getHeader().getFormals()) f.accept(this);
    }
    if (node.getLocalDecls() != null) {
      for (ASTNode decl : node.getLocalDecls()) decl.accept(this);
    }
    if (node.getStmts() != null) {
      for (Stmt stmt : node.getStmts()) stmt.accept(this);
    }

    // =================================================================
    // 5. Clean up: Pop the return type and exit scope
    // =================================================================
    expectedReturnType.pop(); // <-- ΕΔΩ ΒΓΑΙΝΕΙ ΑΠΟ ΤΗ ΣΤΟΙΒΑ
    symbolTable.exitScope();
  }

  @Override
  public void visit(VarDefNode node) {
    for (String id : node.getIds()) {
      try { symbolTable.addEntry(id, node.getType()); }
      catch (SemanticError ignored) {}
    }
  }

  @Override
  public void visit(FormalNode node) {
    for (String id : node.getIds()) {
      try { symbolTable.addEntry(id, node.getType()); }
      catch (SemanticError ignored) {}
    }
  }

  // =========================================================
  // 2. TYPE CHECKING LOGIC
  // =========================================================

  // --- Primitive Values ---
  @Override
  public void visit(IntegerNode node) { typeStack.push("int"); }

  @Override
  public void visit(BooleanNode node) { typeStack.push("bool"); }

  @Override
  public void visit(CharNode node) { typeStack.push("char"); }

  // --- Identifiers (Variables) ---
  @Override
  public void visit(IdNode node) {
    try {
      SymbolEntry entry = symbolTable.getEntry(node.getName());
      // Push the variable's defined type to the stack
      typeStack.push(entry.type() != null ? entry.type().toString() : "void");
    } catch (SemanticError e) {
      typeStack.push("unknown"); // Fallback for undefined vars
    }
  }

  // --- Mathematical Operations (+, -, *, /, mod) ---
  @Override
  public void visit(AddNode node) { checkMathOp(node.getLeft(), node.getRight(), "+"); }
  @Override
  public void visit(SubNode node) { checkMathOp(node.getLeft(), node.getRight(), "-"); }
  @Override
  public void visit(MultNode node) { checkMathOp(node.getLeft(), node.getRight(), "*"); }
  @Override
  public void visit(DivNode node) { checkMathOp(node.getLeft(), node.getRight(), "/"); }
  @Override
  public void visit(ModNode node) { checkMathOp(node.getLeft(), node.getRight(), "mod"); }

  private void checkMathOp(Expr left, Expr right, String op) {
    left.accept(this);
    String tLeft = typeStack.pop();

    right.accept(this);
    String tRight = typeStack.pop();

    // Math operations require both operands to be integers
    if (!tLeft.equals("int") || !tRight.equals("int")) {
      if (!tLeft.equals("unknown") && !tRight.equals("unknown")) {
        System.err.println("[Type Error] Cannot apply operator '" + op + "' to '" + tLeft + "' and '" + tRight + "'");
      }
    }

    // The result of a math operation is always an int
    typeStack.push("int");
  }

  // --- Boolean Operations (and, or, not) ---
  @Override
  public void visit(AndNode node) { checkBoolOp(node.getLeft(), node.getRight(), "and"); }
  @Override
  public void visit(OrNode node) { checkBoolOp(node.getLeft(), node.getRight(), "or"); }

  private void checkBoolOp(Expr left, Expr right, String op) {
    left.accept(this);
    String tLeft = typeStack.pop();

    right.accept(this);
    String tRight = typeStack.pop();

    // Boolean operations require both operands to be booleans
    if (!tLeft.equals("bool") || !tRight.equals("bool")) {
      if (!tLeft.equals("unknown") && !tRight.equals("unknown")) {
        System.err.println("[Type Error] Cannot apply operator '" + op + "' to '" + tLeft + "' and '" + tRight + "'");
      }
    }
    typeStack.push("bool");
  }

  @Override
  public void visit(NotNode node) {
    node.getExpr().accept(this);
    String t = typeStack.pop();
    if (!t.equals("bool") && !t.equals("unknown")) {
      System.err.println("[Type Error] Cannot apply 'not' operator to type '" + t + "'");
    }
    typeStack.push("bool");
  }

  // --- Relational Operations (=, <>, <, >, <=, >=) ---
  @Override
  public void visit(EqNode node)  { checkRelOp(node.getLeft(), node.getRight(), "="); }
  @Override
  public void visit(NeqNode node) { checkRelOp(node.getLeft(), node.getRight(), "<>"); }
  @Override
  public void visit(LtNode node)  { checkRelOp(node.getLeft(), node.getRight(), "<"); }
  @Override
  public void visit(GtNode node)  { checkRelOp(node.getLeft(), node.getRight(), ">"); }
  @Override
  public void visit(LeqNode node) { checkRelOp(node.getLeft(), node.getRight(), "<="); }
  @Override
  public void visit(GeqNode node) { checkRelOp(node.getLeft(), node.getRight(), ">="); }

  // =========================================================
  // 3. CONTROL STRUCTURES (If, Elsif, For)
  // =========================================================

  @Override
  public void visit(IfStmt node) {
    // 1. Check the condition (must be boolean)
    node.getCond().accept(this);
    String condType = typeStack.pop();
    if (!condType.equals("bool") && !condType.equals("unknown")) {
      System.err.println("[Type Error] Condition in 'if' statement must be of type 'bool', found '" + condType + "'");
    }

    // 2. Visit the main 'if' body
    if (node.getIfBody() != null) {
      for (Stmt stmt : node.getIfBody()) stmt.accept(this);
    }

    // 3. Visit 'elsif' branches (if any)
    if (node.getElsifStmts() != null) {
      for (ElsifStmt elsif : node.getElsifStmts()) elsif.accept(this);
    }

    // 4. Visit 'else' branch (if any)
    if (node.getElseBody() != null) {
      for (Stmt stmt : node.getElseBody()) stmt.accept(this);
    }
  }

  @Override
  public void visit(ElsifStmt node) {
    // 1. Check the condition (must be boolean)
    node.getCond().accept(this);
    String condType = typeStack.pop();
    if (!condType.equals("bool") && !condType.equals("unknown")) {
      System.err.println("[Type Error] Condition in 'elsif' statement must be of type 'bool', found '" + condType + "'");
    }

    // 2. Visit the body
    if (node.getBody() != null) {
      for (Stmt stmt : node.getBody()) stmt.accept(this);
    }
  }

  @Override
  public void visit(ForStmt node) {
    // 1. Visit initialization statements (e.g., i := 0)
    if (node.getInitList() != null) {
      for (Stmt stmt : node.getInitList()) stmt.accept(this);
    }

    // 2. Check the condition (must be boolean)
    node.getCond().accept(this);
    String condType = typeStack.pop();
    if (!condType.equals("bool") && !condType.equals("unknown")) {
      System.err.println("[Type Error] Condition in 'for' statement must be of type 'bool', found '" + condType + "'");
    }

    // 3. Visit step statements (e.g., i := i + 1)
    if (node.getSteps() != null) {
      for (Stmt stmt : node.getSteps()) stmt.accept(this);
    }

    // 4. Visit the loop body
    if (node.getBody() != null) {
      for (Stmt stmt : node.getBody()) stmt.accept(this);
    }
  }

  // =========================================================
  // 4. FUNCTIONS & CALLS (Return, CallNode, CallStmt)
  // =========================================================

  @Override
  public void visit(ReturnStmt node) {
    String expected = expectedReturnType.isEmpty() ? "void" : expectedReturnType.peek();

    // If the return statement has an expression (e.g., return 5 + 3)
    if (node.getExpr() != null) {
      node.getExpr().accept(this);
      String actual = typeStack.pop();

      if (!expected.equals(actual) && !actual.equals("unknown")) {
        System.err.println("[Type Error] Return type mismatch! Expected '" + expected + "', got '" + actual + "'");
      }
    }
    // If it is a bare 'return' statement (without a return value)
    else {
      if (!expected.equals("void")) {
        System.err.println("[Type Error] Missing return value! Expected '" + expected + "'");
      }
    }
  }

  @Override
  public void visit(CallNode node) {
    // 1. Visit arguments to catch type errors within the parentheses (e.g., foo(5 + true))
    if (node.getArguments() != null) {
      for (Expr arg : node.getArguments()) {
        arg.accept(this);
        typeStack.pop(); // Discard argument types after checking them
      }
    }

    // 2. Check if it is a built-in Tony function
    String builtInType = getBuiltInReturnType(node.getFunctionName());
    if (builtInType != null) {
      typeStack.push(builtInType);
      return;
    }

    // 3. Otherwise, look up the function in the Symbol Table
    try {
      SymbolEntry entry = symbolTable.getEntry(node.getFunctionName());
      typeStack.push(entry.type() != null ? entry.type().toString() : "void");
    } catch (SemanticError e) {
      typeStack.push("unknown"); // Fallback for undefined functions
    }
  }

  @Override
  public void visit(CallStmt node) {
    node.getCall().accept(this);
    if (!typeStack.isEmpty()) {
      typeStack.pop();
    }
  }

  // Helper method to resolve return types for Tony's built-in functions
  private String getBuiltInReturnType(String name) {
    return switch (name) {
      case "puti", "putb", "putc", "puts", "gets", "strcpy", "strcat" -> "void";
      case "geti", "abs", "ord", "strlen", "strcmp" -> "int";
      case "getb" -> "bool";
      case "getc", "chr" -> "char";
      default -> null; // Not a built-in function
    };
  }

  // =========================================================
  // 5. ARRAYS & LISTS (Compound Types)
  // =========================================================

  // --- Arrays ---
  @Override
  public void visit(NewArrayNode node) {
    // e.g., new int[5]
    node.getExpr().accept(this);
    String sizeType = typeStack.pop();

    if (!sizeType.equals("int") && !sizeType.equals("unknown")) {
      System.err.println("[Type Error] Array size must be an integer, found '" + sizeType + "'");
    }

    // Push the type with "[]" appended (e.g., "int" becomes "int[]")
    typeStack.push(node.getType().toString() + "[]");
  }

  @Override
  public void visit(ArrayAccessNode node) {
    // e.g., a[i]
    node.getArray().accept(this);
    String arrayType = typeStack.pop();

    node.getIndex().accept(this);
    String indexType = typeStack.pop();

    if (!indexType.equals("int") && !indexType.equals("unknown")) {
      System.err.println("[Type Error] Array index must be an integer, found '" + indexType + "'");
    }

    if (arrayType.endsWith("[]")) {
      // Remove the last "[]" to get the underlying element type
      typeStack.push(arrayType.substring(0, arrayType.length() - 2));
    } else if (!arrayType.equals("unknown")) {
      System.err.println("[Type Error] Cannot access index on non-array type '" + arrayType + "'");
      typeStack.push("unknown");
    } else {
      typeStack.push("unknown");
    }
  }

  // --- Lists ---
  @Override
  public void visit(NilNode node) {
    // 'nil' is compatible with any list type
    typeStack.push("nil");
  }

  @Override
  public void visit(ConsNode node) {
    // e.g., head # tail
    node.getLeft().accept(this);
    String headType = typeStack.pop();

    node.getRight().accept(this);
    String tailType = typeStack.pop();

    String expectedTail = "list[" + headType + "]";

    if (!tailType.equals("nil") && !tailType.equals(expectedTail) && !tailType.equals("unknown") && !headType.equals("unknown")) {
      System.err.println("[Type Error] Cannot cons '" + headType + "' to '" + tailType + "'. Expected '" + expectedTail + "'");
    }

    // The result of a cons operation is the list type
    typeStack.push(expectedTail);
  }

  @Override
  public void visit(HeadNode node) {
    node.getExpr().accept(this);
    String listType = typeStack.pop();

    if (listType.startsWith("list[") && listType.endsWith("]")) {
      // Extract the element type from "list[type]"
      typeStack.push(listType.substring(5, listType.length() - 1));
    } else if (listType.equals("nil")) {
      System.err.println("[Type Error] Cannot call 'head' on empty list 'nil'");
      typeStack.push("unknown");
    } else if (!listType.equals("unknown")) {
      System.err.println("[Type Error] Cannot call 'head' on non-list type '" + listType + "'");
      typeStack.push("unknown");
    } else {
      typeStack.push("unknown");
    }
  }

  @Override
  public void visit(TailNode node) {
    node.getExpr().accept(this);
    String listType = typeStack.pop();

    if (listType.startsWith("list[") && listType.endsWith("]")) {
      typeStack.push(listType);
    } else if (listType.equals("nil")) {
      System.err.println("[Type Error] Cannot call 'tail' on empty list 'nil'");
      typeStack.push("unknown");
    } else if (!listType.equals("unknown")) {
      System.err.println("[Type Error] Cannot call 'tail' on non-list type '" + listType + "'");
      typeStack.push("unknown");
    } else {
      typeStack.push("unknown");
    }
  }

  @Override
  public void visit(NilqNode node) {
    node.getExpr().accept(this);
    String listType = typeStack.pop();

    if (!listType.startsWith("list[") && !listType.equals("nil") && !listType.equals("unknown")) {
      System.err.println("[Type Error] 'nil?' operator expects a list, found '" + listType + "'");
    }

    // 'nil?' always evaluates to a boolean
    typeStack.push("bool");
  }

  @Override
  public void visit(AssignStmt node) {
    node.getTarget().accept(this);
    String targetType = typeStack.pop();

    node.getE().accept(this);
    String exprType = typeStack.pop();

    // SPECIAL CASE: Assigning 'nil' to a list type is perfectly valid
    boolean isNilAssignment = targetType.startsWith("list[") && exprType.equals("nil");

    // Check if types match (ignore "unknown" types to prevent cascading errors)
    if (!targetType.equals(exprType) && !isNilAssignment && !targetType.equals("unknown") && !exprType.equals("unknown")) {
      System.err.println("[Type Error] Type mismatch! Cannot assign '" + exprType + "' to variable of type '" + targetType + "'");
    }
  }

  private void checkRelOp(Expr left, Expr right, String op) {
    left.accept(this);
    String tLeft = typeStack.pop();

    right.accept(this);
    String tRight = typeStack.pop();

    // SPECIAL CASE: Comparing any list type with 'nil' is allowed
    boolean isNilComparison = (tLeft.startsWith("list[") && tRight.equals("nil")) ||
      (tRight.startsWith("list[") && tLeft.equals("nil"));

    // Relational operations require both operands to be of the exact same type
    if (!tLeft.equals(tRight) && !isNilComparison && !tLeft.equals("unknown") && !tRight.equals("unknown")) {
      System.err.println("[Type Error] Type mismatch in comparison: '" + tLeft + "' " + op + " '" + tRight + "'");
    }

    // The result of any comparison is always a boolean
    typeStack.push("bool");
  }
}
