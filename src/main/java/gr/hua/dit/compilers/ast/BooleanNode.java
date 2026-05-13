package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class BooleanNode extends Expr {
  private final boolean value;

  public BooleanNode(boolean value) { this.value = value; }

  @Override
  public String toString() {
    return "Bool(" + value + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
