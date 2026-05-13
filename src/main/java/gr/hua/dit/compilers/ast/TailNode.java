package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class TailNode extends UnaryNode {
  public TailNode(Expr e) { super(e); }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
