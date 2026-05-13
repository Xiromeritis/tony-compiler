package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class AddNode extends BinaryNode {
  public AddNode(Expr e1, Expr e2) { super(e1, e2); }

  @Override
  public String toString() {
    return "Add(" + getLeft().toString() + ", " + getRight().toString() + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
