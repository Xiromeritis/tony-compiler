package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class UnaryMinusNode extends UnaryNode {
  public UnaryMinusNode(Expr e) { super(e); }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
