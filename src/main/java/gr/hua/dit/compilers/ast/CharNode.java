package gr.hua.dit.compilers.ast;

public class CharNode extends Expr {
  private final Character value;

  public CharNode(Character value) { this.value = value; }

  @Override
  public String toString() {
    return "Char('" + value + "')";
  }
}
