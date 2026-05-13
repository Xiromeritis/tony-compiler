package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class GtNode extends BinaryNode {
  public GtNode(Expr e1, Expr e2) { super(e1, e2); }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
