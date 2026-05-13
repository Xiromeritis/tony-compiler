package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class SkipStmt extends Stmt {
  @Override
  public String toString() {
    return "Skip";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
