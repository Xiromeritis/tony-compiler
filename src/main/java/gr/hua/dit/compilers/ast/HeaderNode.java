package gr.hua.dit.compilers.ast;
import gr.hua.dit.compilers.visitors.Visitor;

import java.util.List;

public class HeaderNode extends ASTNode {
  private final TypeNode returnType; // Can be null if it's a procedure (skip)
  private final String name;
  private final List<FormalNode> formals;

  public HeaderNode(TypeNode returnType, String name, List<FormalNode> formals) {
    this.returnType = returnType;
    this.name = name;
    this.formals = formals;
  }

  public TypeNode getReturnType() { return returnType; }

  public String getName() { return name; }

  public List<FormalNode> getFormals() { return formals; }

  @Override
  public String toString() {
    String typeStr = returnType != null ? returnType.toString() : "void";
    String formalsStr = formals != null && !formals.isEmpty() ? formals.toString() : "[]";
    return "Header(" + typeStr + " " + name + ", Formals: " + formalsStr + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
