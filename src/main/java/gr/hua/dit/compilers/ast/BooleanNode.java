package gr.hua.dit.compilers.ast;

public class BooleanNode extends Expr {
  private final boolean value;

  public BooleanNode(boolean value) { this.value = value; }

  @Override
  public String toString() {
    return "Bool(" + value + ")";
  }
}
