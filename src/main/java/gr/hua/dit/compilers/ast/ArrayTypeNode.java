package gr.hua.dit.compilers.ast;

public class ArrayTypeNode extends TypeNode {
  private final TypeNode baseType;

  public ArrayTypeNode(TypeNode baseType) {
    super(baseType + "[]");
    this.baseType = baseType;
  }
}
