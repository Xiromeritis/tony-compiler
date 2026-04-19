package gr.hua.dit.compilers.ast;

public class ReturnStmt extends Stmt {
  private final Expr e;

  public ReturnStmt(Expr e) { this.e = e; }

  @Override
  public String toString() {
    return "Return(" + e + ")";
  }
}
