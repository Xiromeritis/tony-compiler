package gr.hua.dit.compilers.ast;

public class NewArrayNode extends Expr {
  private final TypeNode type;
  private final Expr size;

  public NewArrayNode(TypeNode type, Expr size) {
    this.type = type;
    this.size = size;
  }

  @Override
  public String toString() {
    return "NewArray(" + type + "[" + size + "])";
  }
}
