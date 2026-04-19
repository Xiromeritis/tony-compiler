package gr.hua.dit.compilers.ast;
import java.util.List;

public class ForStmt extends Stmt {
  private final List<Stmt> initList;
  private final Expr cond;
  private final List<Stmt> steps;
  private final List<Stmt> body;

  public ForStmt(List<Stmt> init, Expr cond, List<Stmt> steps, List<Stmt> body) {
    this.initList = init;
    this.cond = cond;
    this.steps = steps;
    this.body = body;
  }

  @Override
  public String toString() {
    return "For(init: " + initList + ", cond: " + cond + ", step: " + steps + ",\n      body: " + body + ")";
  }
}
