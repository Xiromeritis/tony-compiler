package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class CharNode extends Expr {
  private final Character value;

  public CharNode(Character value) { this.value = value; }

  @Override
  public String toString() {
    return "Char('" + value + "')";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
