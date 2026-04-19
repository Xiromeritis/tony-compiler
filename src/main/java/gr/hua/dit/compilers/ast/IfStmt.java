package gr.hua.dit.compilers.ast;
import java.util.List;

public class IfStmt extends Stmt {
  private final Expr condition;
  private final List<Stmt> ifBody;
  private final List<ElsifStmt> elsifStmts;
  private final List<Stmt> elseBody; // Can be null

  public IfStmt(Expr condition, List<Stmt> ifBody, List<ElsifStmt> elsifStmts, List<Stmt> elseBody) {
    this.condition = condition;
    this.ifBody = ifBody;
    this.elsifStmts = elsifStmts;
    this.elseBody = elseBody;
  }

  @Override
  public String toString() {
    return "If(cond: " + condition + ", body: " + ifBody +
      (elsifStmts != null && !elsifStmts.isEmpty() ? ", elsifs: " + elsifStmts : "") +
      (elseBody != null ? ", else: " + elseBody : "") + ")";
  }
}
