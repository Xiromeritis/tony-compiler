package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class NotNode extends UnaryNode {
  public NotNode(Expr expr) { super(expr); }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
