package gr.hua.dit.compilers.ast;

public abstract class BinaryNode extends Expr {
  private final Expr e1;
  private final Expr e2;

  public BinaryNode(Expr left, Expr right) {
    this.e1 = left;
    this.e2 = right;
  }

  public Expr getLeft() { return e1; }

  public Expr getRight() { return e2; }

  @Override
  public String toString() {
    String opName = this.getClass().getSimpleName().replace("Node", "");
    return opName + "(" + getLeft() + ", " + getRight() + ")";
  }
}
