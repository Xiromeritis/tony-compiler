package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class NilNode extends Expr {
  @Override
  public String toString() {
    return "Nil";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
