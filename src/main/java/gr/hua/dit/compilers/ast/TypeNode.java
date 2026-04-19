package gr.hua.dit.compilers.ast;

public class TypeNode extends ASTNode {
  private final String typeName; // "int", "bool", "char"

  public TypeNode(String typeName) { this.typeName = typeName; }

  @Override
  public String toString() {
    return typeName;
  }
}
