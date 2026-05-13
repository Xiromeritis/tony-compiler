package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class IdNode extends Expr {
  private final String name;

  public IdNode(String name) { this.name = name; }

  public String getName() { return name; }

  @Override
  public String toString() {
    return "Var(" + name + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
