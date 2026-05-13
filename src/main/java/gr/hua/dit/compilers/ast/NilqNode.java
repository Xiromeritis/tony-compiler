package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class NilqNode extends UnaryNode {
  public NilqNode(Expr expr) { super(expr); }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
