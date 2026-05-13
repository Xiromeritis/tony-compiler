package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class ArrayTypeNode extends TypeNode {
  private final TypeNode baseType;

  public ArrayTypeNode(TypeNode baseType) {
    super(baseType + "[]");
    this.baseType = baseType;
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
