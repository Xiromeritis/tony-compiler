package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class TypeNode extends ASTNode {
  private final String typeName; // "int", "bool", "char"

  public TypeNode(String typeName) { this.typeName = typeName; }

  @Override
  public String toString() {
    return typeName;
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
