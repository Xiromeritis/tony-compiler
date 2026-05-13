package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class IntegerNode extends Expr {
  private final Integer value;

  public IntegerNode(Integer value) {
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "Int(" + value + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
