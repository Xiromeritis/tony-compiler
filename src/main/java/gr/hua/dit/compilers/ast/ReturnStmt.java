package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class ReturnStmt extends Stmt {
  private final Expr e;

  public ReturnStmt(Expr e) { this.e = e; }

  @Override
  public String toString() {
    return "Return(" + e + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
