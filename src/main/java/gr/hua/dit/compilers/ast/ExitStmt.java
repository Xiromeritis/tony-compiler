package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class ExitStmt extends Stmt {
  @Override
  public String toString() {
    return "Exit";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
