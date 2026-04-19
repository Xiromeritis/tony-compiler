package gr.hua.dit.compilers.ast;

public class CallStmt extends Stmt {
  private final CallNode call;

  public CallStmt(CallNode call) { this.call = call; }

  @Override
  public String toString() {
    return "CallStmt(" + call.toString() + ")";
  }
}
