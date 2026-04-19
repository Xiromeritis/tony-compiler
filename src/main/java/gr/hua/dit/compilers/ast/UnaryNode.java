package gr.hua.dit.compilers.ast;

public abstract class UnaryNode extends Expr {
  private final Expr e;

  public UnaryNode(Expr e) {
    this.e = e;
  }

  public Expr getExpr() { return e; }

  @Override
  public String toString() {
    String opName = this.getClass().getSimpleName().replace("Node", "");
    return opName + "(" + getExpr() + ")";
  }
}
