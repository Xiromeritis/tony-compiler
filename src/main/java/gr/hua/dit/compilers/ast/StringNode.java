package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class StringNode extends Expr {
  private final String value;

  public StringNode(String value) { this.value = value; }

  public String getValue() { return value; }

  @Override
  public String toString() {
    return "String(" + value + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
