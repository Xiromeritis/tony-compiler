package gr.hua.dit.compilers.ast;

public class StringNode extends Expr {
  private final String value;

  public StringNode(String value) { this.value = value; }

  @Override
  public String toString() {
    return "String(" + value + ")";
  }
}
