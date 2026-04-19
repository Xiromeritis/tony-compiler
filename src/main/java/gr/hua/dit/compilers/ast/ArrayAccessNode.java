package gr.hua.dit.compilers.ast;

public class ArrayAccessNode extends Expr {
  private final Expr array;
  private final Expr index;

  public ArrayAccessNode(Expr array, Expr index) {
    this.array = array;
    this.index = index;
  }

  @Override
  public String toString() {
    return "ArrayAccess(" + array + "[" + index + "])";
  }
}
