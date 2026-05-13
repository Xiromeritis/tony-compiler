package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class AssignStmt extends Stmt {
  private final Expr target;
  private final Expr e;

  public AssignStmt(Expr target, Expr e) {
    this.target = target;
    this.e = e;
  }

  public Expr getTarget() { return target; }

  public Expr getE() { return e; }

  @Override
  public String toString() {
    return "Assign(" + target.toString() + " := " + e.toString() + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
