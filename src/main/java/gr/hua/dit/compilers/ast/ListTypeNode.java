package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class ListTypeNode extends TypeNode {
  private final TypeNode baseType;

  public ListTypeNode(TypeNode baseType) {
    super("list[" + baseType + "]");
    this.baseType = baseType;
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
