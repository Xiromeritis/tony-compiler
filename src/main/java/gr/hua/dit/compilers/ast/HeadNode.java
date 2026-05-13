package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class HeadNode extends UnaryNode {
  public HeadNode(Expr expr) { super(expr); }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
