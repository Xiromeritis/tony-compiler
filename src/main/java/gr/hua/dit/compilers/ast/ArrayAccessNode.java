package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class ArrayAccessNode extends Expr {
  private final Expr array;
  private final Expr index;

  public ArrayAccessNode(Expr array, Expr index) {
    this.array = array;
    this.index = index;
  }

  public Expr getArray() { return array; }
  public Expr getIndex() { return index; }

  @Override
  public String toString() {
    return "ArrayAccess(" + array + "[" + index + "])";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
