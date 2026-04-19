package gr.hua.dit.compilers.ast;

public class ListTypeNode extends TypeNode {
  private final TypeNode baseType;

  public ListTypeNode(TypeNode baseType) {
    super("list[" + baseType + "]");
    this.baseType = baseType;
  }
}
