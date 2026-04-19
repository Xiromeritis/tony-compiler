package gr.hua.dit.compilers.ast;

public class IdNode extends Expr {
  private final String name;

  public IdNode(String name) { this.name = name; }

  @Override
  public String toString() {
    return "Var(" + name + ")";
  }
}
