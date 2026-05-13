package gr.hua.dit.compilers.ast;

import gr.hua.dit.compilers.visitors.Visitor;

public class CallStmt extends Stmt {
  private final CallNode call;

  public CallStmt(CallNode call) { this.call = call; }

  @Override
  public String toString() {
    return "CallStmt(" + call.toString() + ")";
  }

  @Override
  public void accept(Visitor visitor) { visitor.visit(this); }
}
