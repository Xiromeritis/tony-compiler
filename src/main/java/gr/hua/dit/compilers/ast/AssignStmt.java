package gr.hua.dit.compilers.ast;

public class AssignStmt extends Stmt {
  private final Expr target;
  private final Expr e;

  public AssignStmt(Expr target, Expr e) {
    this.target = target;
    this.e = e;
  }

  @Override
  public String toString() {
    return "Assign(" + target.toString() + " := " + e.toString() + ")";
  }
}
