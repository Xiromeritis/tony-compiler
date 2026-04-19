package gr.hua.dit.compilers.ast;

public class FuncDeclNode extends ASTNode {
  private final HeaderNode header;

  public FuncDeclNode(HeaderNode header) { this.header = header; }

  @Override
  public String toString() {
    return "FuncDecl(" + header + ")";
  }
}
