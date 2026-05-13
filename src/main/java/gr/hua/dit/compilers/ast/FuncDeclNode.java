package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class FuncDeclNode extends ASTNode {
  private final HeaderNode header;

  public FuncDeclNode(HeaderNode header) { this.header = header; }

  @Override
  public String toString() {
    return "FuncDecl(" + header + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
