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
  // Stack to keep track of the expected return type of the current function
  private final Stack<String> expectedReturnType;

  public TypeChecker() {
    this.symbolTable = new SymbolTable();
    this.typeStack = new Stack<>();
    this.expectedReturnType = new Stack<>();
  }

  // =========================================================
  // SCOPE MANAGEMENT
  // =========================================================

  @Override
  public void visit(ProgramNode node) {
    symbolTable.enterScope();
    if (node.getMainFunc() != null) node.getMainFunc().accept(this);
    symbolTable.exitScope();
  }

  @Override
  public void visit(FuncDefNode node) {
    try {
      symbolTable.addEntry(node.getHeader().getName(), node.getHeader().getReturnType());
    } catch (SemanticError ignored) {}

    symbolTable.enterScope();

    String retType = "void";
    if (node.getHeader().getReturnType() != null) {
      retType = node.getHeader().getReturnType().toString();
    }
    expectedReturnType.push(retType);

    if (node.getHeader().getFormals() != null) {
      for (FormalNode f : node.getHeader().getFormals()) f.accept(this);
    }
    if (node.getLocalDecls() != null) {
      for (ASTNode decl : node.getLocalDecls()) decl.accept(this);
    }
    if (node.getStmts() != null) {
      for (Stmt stmt : node.getStmts()) stmt.accept(this);
    }

    expectedReturnType.pop();
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
  // TYPE CHECKING LOGIC
  // =========================================================

  @Override
  public void visit(IntegerNode node) { typeStack.push("int"); }

  @Override
  public void visit(BooleanNode node) { typeStack.push("bool"); }

  @Override
  public void visit(CharNode node) { typeStack.push("char"); }

  @Override
  public void visit(StringNode node) { typeStack.push("char[]"); }

  @Override
  public void visit(UnaryMinusNode node) {
    node.getExpr().accept(this);
    String t = typeStack.pop();
    if (!t.equals("int") && !t.equals("unknown")) {
      System.err.println("[Type Error] Cannot apply unary minus to '" + t + "'");
    }
    typeStack.push("int");
  }

  @Override
  public void visit(UnaryPlusNode node) {
    node.getExpr().accept(this);
    String t = typeStack.pop();
    if (!t.equals("int") && !t.equals("unknown")) {
      System.err.println("[Type Error] Cannot apply unary plus to '" + t + "'");
    }
    typeStack.push("int");
  }

  @Override
  public void visit(IdNode node) {
    try {
      SymbolEntry entry = symbolTable.getEntry(node.getName());
      typeStack.push(entry.type() != null ? entry.type().toString() : "void");
    } catch (SemanticError e) {
      typeStack.push("unknown");
    }
  }

  // --- Mathematical Operations ---
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

    if (!tLeft.equals("int") || !tRight.equals("int")) {
      if (!tLeft.equals("unknown") && !tRight.equals("unknown")) {
        System.err.println("[Type Error] Cannot apply operator '" + op + "' to '" + tLeft + "' and '" + tRight + "'");
      }
    }
    typeStack.push("int");
  }

  // --- Boolean Operations ---
  @Override
  public void visit(AndNode node) { checkBoolOp(node.getLeft(), node.getRight(), "and"); }
  @Override
  public void visit(OrNode node) { checkBoolOp(node.getLeft(), node.getRight(), "or"); }

  private void checkBoolOp(Expr left, Expr right, String op) {
    left.accept(this);
    String tLeft = typeStack.pop();
    right.accept(this);
    String tRight = typeStack.pop();

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

  // --- Relational Operations ---
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

  // --- Control Structures ---
  @Override
  public void visit(IfStmt node) {
    node.getCond().accept(this);
    String condType = typeStack.pop();
    if (!condType.equals("bool") && !condType.equals("unknown")) {
      System.err.println("[Type Error] Condition in 'if' statement must be of type 'bool', found '" + condType + "'");
    }

    if (node.getIfBody() != null) {
      for (Stmt stmt : node.getIfBody()) stmt.accept(this);
    }

    if (node.getElsifStmts() != null) {
      for (ElsifStmt elsif : node.getElsifStmts()) elsif.accept(this);
    }

    if (node.getElseBody() != null) {
      for (Stmt stmt : node.getElseBody()) stmt.accept(this);
    }
  }

  @Override
  public void visit(ElsifStmt node) {
    node.getCond().accept(this);
    String condType = typeStack.pop();
    if (!condType.equals("bool") && !condType.equals("unknown")) {
      System.err.println("[Type Error] Condition in 'elsif' statement must be of type 'bool', found '" + condType + "'");
    }

    if (node.getBody() != null) {
      for (Stmt stmt : node.getBody()) stmt.accept(this);
    }
  }

  @Override
  public void visit(ForStmt node) {
    if (node.getInitList() != null) {
      for (Stmt stmt : node.getInitList()) stmt.accept(this);
    }

    node.getCond().accept(this);
    String condType = typeStack.pop();
    if (!condType.equals("bool") && !condType.equals("unknown")) {
      System.err.println("[Type Error] Condition in 'for' statement must be of type 'bool', found '" + condType + "'");
    }

    if (node.getSteps() != null) {
      for (Stmt stmt : node.getSteps()) stmt.accept(this);
    }

    if (node.getBody() != null) {
      for (Stmt stmt : node.getBody()) stmt.accept(this);
    }
  }

  @Override
  public void visit(ReturnStmt node) {
    String expected = expectedReturnType.isEmpty() ? "void" : expectedReturnType.peek();

    if (node.getExpr() != null) {
      node.getExpr().accept(this);
      String actual = typeStack.pop();

      if (!expected.equals(actual) && !actual.equals("unknown")) {
        System.err.println("[Type Error] Return type mismatch! Expected '" + expected + "', got '" + actual + "'");
      }
    } else {
      if (!expected.equals("void")) {
        System.err.println("[Type Error] Missing return value! Expected '" + expected + "'");
      }
    }
  }

  @Override
  public void visit(CallNode node) {
    if (node.getArguments() != null) {
      for (Expr arg : node.getArguments()) {
        arg.accept(this);
        typeStack.pop();
      }
    }

    String builtInType = getBuiltInReturnType(node.getFunctionName());
    if (builtInType != null) {
      typeStack.push(builtInType);
      return;
    }

    try {
      SymbolEntry entry = symbolTable.getEntry(node.getFunctionName());
      typeStack.push(entry.type() != null ? entry.type().toString() : "void");
    } catch (SemanticError e) {
      typeStack.push("unknown");
    }
  }

  @Override
  public void visit(CallStmt node) {
    if (node.getCall() != null) node.getCall().accept(this);
    if (!typeStack.isEmpty()) typeStack.pop();
  }

  private String getBuiltInReturnType(String name) {
    return switch (name) {
      case "puti", "putb", "putc", "puts", "gets", "strcpy", "strcat" -> "void";
      case "geti", "abs", "ord", "strlen", "strcmp" -> "int";
      case "getb" -> "bool";
      case "getc", "chr" -> "char";
      default -> null;
    };
  }

  // --- Compound Types ---
  @Override
  public void visit(NewArrayNode node) {
    node.getExpr().accept(this);
    String sizeType = typeStack.pop();

    if (!sizeType.equals("int") && !sizeType.equals("unknown")) {
      System.err.println("[Type Error] Array size must be an integer, found '" + sizeType + "'");
    }
    typeStack.push(node.getType().toString() + "[]");
  }

  @Override
  public void visit(ArrayAccessNode node) {
    node.getArray().accept(this);
    String arrayType = typeStack.pop();
    node.getIndex().accept(this);
    String indexType = typeStack.pop();

    if (!indexType.equals("int") && !indexType.equals("unknown")) {
      System.err.println("[Type Error] Array index must be an integer, found '" + indexType + "'");
    }

    if (arrayType.endsWith("[]")) {
      typeStack.push(arrayType.substring(0, arrayType.length() - 2));
    } else if (!arrayType.equals("unknown")) {
      System.err.println("[Type Error] Cannot access index on non-array type '" + arrayType + "'");
      typeStack.push("unknown");
    } else {
      typeStack.push("unknown");
    }
  }

  @Override
  public void visit(NilNode node) { typeStack.push("nil"); }

  @Override
  public void visit(ConsNode node) {
    node.getLeft().accept(this);
    String headType = typeStack.pop();
    node.getRight().accept(this);
    String tailType = typeStack.pop();

    String expectedTail = "list[" + headType + "]";
    if (!tailType.equals("nil") && !tailType.equals(expectedTail) && !tailType.equals("unknown") && !headType.equals("unknown")) {
      System.err.println("[Type Error] Cannot cons '" + headType + "' to '" + tailType + "'. Expected '" + expectedTail + "'");
    }
    typeStack.push(expectedTail);
  }

  @Override
  public void visit(HeadNode node) {
    node.getExpr().accept(this);
    String listType = typeStack.pop();

    if (listType.startsWith("list[") && listType.endsWith("]")) {
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
    typeStack.push("bool");
  }

  @Override
  public void visit(AssignStmt node) {
    node.getTarget().accept(this);
    String targetType = typeStack.pop();
    node.getE().accept(this);
    String exprType = typeStack.pop();

    boolean isNilAssignment = targetType.startsWith("list[") && exprType.equals("nil");

    if (!targetType.equals(exprType) && !isNilAssignment && !targetType.equals("unknown") && !exprType.equals("unknown")) {
      System.err.println("[Type Error] Type mismatch! Cannot assign '" + exprType + "' to variable of type '" + targetType + "'");
    }
  }

  private void checkRelOp(Expr left, Expr right, String op) {
    left.accept(this);
    String tLeft = typeStack.pop();
    right.accept(this);
    String tRight = typeStack.pop();

    boolean isNilComparison = (tLeft.startsWith("list[") && tRight.equals("nil")) ||
      (tRight.startsWith("list[") && tLeft.equals("nil"));

    if (!tLeft.equals(tRight) && !isNilComparison && !tLeft.equals("unknown") && !tRight.equals("unknown")) {
      System.err.println("[Type Error] Type mismatch in comparison: '" + tLeft + "' " + op + " '" + tRight + "'");
    }
    typeStack.push("bool");
  }
}
